const App = getApp()
const { mockDishes, mockCombos } = require('../../utils/mockData.js')

Page({
  data: {
    id: '',
    isCombo: false,
    item: null,
    flavors: [],
    selectedFlavor: '',
    quantity: 1,
    tableNo: '',
    loading: false
  },

  onLoad: function (options) {
    this.setData({
      tableNo: App.globalData.tableNo || wx.getStorageSync('tableNo')
    })

    console.log('detail options:', options)

    if (options.item) {
      try {
        const itemStr = decodeURIComponent(options.item)
        console.log('item string:', itemStr)
        const item = JSON.parse(itemStr)
        console.log('parsed item:', item)
        
        if (item) {
          if (Array.isArray(item.flavors)) {
            item.flavors = item.flavors.join(',')
          }
          
          this.setData({
            item: item,
            isCombo: options.isCombo === 'true',
            id: item.id || ''
          })
          
          if (!this.data.isCombo && item.flavors) {
            const flavorStr = typeof item.flavors === 'string' ? item.flavors : item.flavors.join(',')
            this.setData({ flavors: flavorStr.split(',').filter(f => f) })
          }
          return
        }
      } catch (e) {
        console.error('parse item error:', e)
      }
    }

    if (options.id) {
      this.setData({
        id: options.id,
        isCombo: options.isCombo === 'true'
      })
      this.loadDetailFromServer()
    } else {
      wx.showToast({ title: '商品信息错误', icon: 'none' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },

  loadDetailFromServer: function () {
    if (!this.data.id) {
      wx.showToast({ title: '商品ID错误', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    
    const url = this.data.isCombo 
      ? 'http://127.0.0.1:8080/api/combo/' + this.data.id
      : 'http://127.0.0.1:8080/api/dish/' + this.data.id
    
    wx.request({
      url: url,
      method: 'GET',
      timeout: 10000,
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        console.log('server response:', res)
        if (res.data && res.data.code === 200 && res.data.data) {
          const item = res.data.data
          if (Array.isArray(item.flavors)) {
            item.flavors = item.flavors.join(',')
          }
          this.setData({ 
            item: item,
            id: item.id
          })
          if (!this.data.isCombo && item.flavors) {
            const flavorStr = typeof item.flavors === 'string' ? item.flavors : item.flavors.join(',')
            this.setData({ flavors: flavorStr.split(',').filter(f => f) })
          }
        } else {
          this.loadMockDetail()
        }
      },
      fail: (err) => {
        console.error('server request fail:', err)
        this.loadMockDetail()
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  loadMockDetail: function () {
    if (!this.data.id) {
      wx.showToast({ title: '未找到商品', icon: 'none' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }

    let item = null
    const id = parseInt(this.data.id)
    
    if (this.data.isCombo) {
      item = mockCombos.find(c => c.id === id)
    } else {
      item = mockDishes.find(d => d.id === id)
    }
    
    if (item) {
      if (!this.data.isCombo && item.flavors) {
        const flavorStr = typeof item.flavors === 'string' ? item.flavors : item.flavors.join(',')
        this.setData({ flavors: flavorStr.split(',').filter(f => f) })
      }
      this.setData({ item })
    } else {
      wx.showToast({ title: '未找到商品', icon: 'none' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },

  goBack: function () {
    wx.navigateBack()
  },

  selectFlavor: function (e) {
    const flavor = e.currentTarget.dataset.flavor
    this.setData({ selectedFlavor: flavor })
  },

  minusQuantity: function () {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },

  plusQuantity: function () {
    this.setData({ quantity: this.data.quantity + 1 })
  },

  addToCart: function () {
    if (!this.data.tableNo) {
      wx.showToast({ title: '请先选择桌号', icon: 'none' })
      return
    }

    if (!this.data.item) {
      wx.showToast({ title: '商品信息错误', icon: 'none' })
      return
    }

    const itemId = this.data.item.id || this.data.id
    if (!itemId) {
      wx.showToast({ title: '商品ID错误', icon: 'none' })
      return
    }

    if (!this.data.isCombo && !this.data.selectedFlavor && this.data.flavors.length > 0) {
      wx.showToast({ title: '请选择口味', icon: 'none' })
      return
    }

    const cartItem = {
      tableNo: this.data.tableNo,
      dishId: this.data.isCombo ? null : itemId,
      comboId: this.data.isCombo ? itemId : null,
      name: this.data.item.name,
      price: this.data.item.price,
      flavor: this.data.selectedFlavor || '',
      quantity: this.data.quantity,
      image: this.data.item.image,
      isCombo: this.data.isCombo,
      comboItems: this.data.isCombo ? this.data.item.items : null
    }

    let cart = wx.getStorageSync('cart_' + this.data.tableNo) || []
    const existingIndex = cart.findIndex(item => 
      (item.dishId === cartItem.dishId && item.comboId === cartItem.comboId && item.flavor === cartItem.flavor)
    )

    if (existingIndex >= 0) {
      cart[existingIndex].quantity += cartItem.quantity
    } else {
      cart.push(cartItem)
    }

    wx.setStorageSync('cart_' + this.data.tableNo, cart)
    wx.showToast({ title: '已加入购物车', icon: 'success' })
    
    setTimeout(() => {
      wx.navigateBack()
    }, 1000)
  }
})