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
    tableNo: ''
  },

  onLoad: function (options) {
    this.setData({
      id: options.id,
      isCombo: options.isCombo === 'true',
      tableNo: App.globalData.tableNo || wx.getStorageSync('tableNo')
    })
    this.loadDetail()
  },

  loadDetail: function () {
    let item = null
    
    if (this.data.isCombo) {
      item = mockCombos.find(c => c.id === parseInt(this.data.id))
    } else {
      item = mockDishes.find(d => d.id === parseInt(this.data.id))
    }
    
    if (item) {
      if (!this.data.isCombo && item.flavors) {
        this.setData({ flavors: item.flavors.split(',') })
      }
      this.setData({ item })
    } else {
      wx.showToast({ title: '未找到商品', icon: 'none' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
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

    if (!this.data.isCombo && !this.data.selectedFlavor && this.data.flavors.length > 0) {
      wx.showToast({ title: '请选择口味', icon: 'none' })
      return
    }

    const cartItem = {
      tableNo: this.data.tableNo,
      dishId: this.data.isCombo ? null : this.data.id,
      comboId: this.data.isCombo ? this.data.id : null,
      name: this.data.item.name,
      price: this.data.item.price,
      flavor: this.data.selectedFlavor || '',
      quantity: this.data.quantity,
      image: this.data.item.image
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
