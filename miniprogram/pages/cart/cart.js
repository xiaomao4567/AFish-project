const App = getApp()

Page({
  data: {
    tableNo: '',
    items: [],
    total: 0,
    count: 0
  },

  onLoad: function () {
    this.setData({ tableNo: App.globalData.tableNo || wx.getStorageSync('tableNo') })
    this.loadCart()
  },

  onShow: function () {
    this.loadCart()
  },

  loadCart: function () {
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    if (!tableNo) {
      this.setData({ items: [], total: 0, count: 0 })
      return
    }

    const items = wx.getStorageSync('cart_' + tableNo) || []
    let total = 0
    let count = 0
    items.forEach((item, index) => {
      item.id = index
      total += item.price * item.quantity
      count += item.quantity
    })
    this.setData({ items, total, count })
    App.globalData.cartCount = count
    App.globalData.cartTotal = total
  },

  updateQuantity: function (e) {
    const index = e.currentTarget.dataset.index
    const delta = e.currentTarget.dataset.delta
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    let cart = wx.getStorageSync('cart_' + tableNo) || []
    
    cart[index].quantity += delta
    
    if (cart[index].quantity <= 0) {
      cart.splice(index, 1)
    }
    
    wx.setStorageSync('cart_' + tableNo, cart)
    this.loadCart()
  },

  deleteItem: function (e) {
    const index = e.currentTarget.dataset.index
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    let cart = wx.getStorageSync('cart_' + tableNo) || []
    cart.splice(index, 1)
    wx.setStorageSync('cart_' + tableNo, cart)
    this.loadCart()
  },

  goOrder: function () {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/order/order' })
  },

  clearCart: function () {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空购物车吗？',
      success: (res) => {
        if (res.confirm) {
          const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
          wx.setStorageSync('cart_' + tableNo, [])
          this.setData({ items: [], total: 0, count: 0 })
          App.globalData.cartCount = 0
          App.globalData.cartTotal = 0
          wx.showToast({ title: '已清空', icon: 'success' })
        }
      }
    })
  }
})
