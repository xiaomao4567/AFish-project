const App = getApp()

Page({
  data: {
    tableNo: '',
    items: [],
    total: 0,
    count: 0,
    expandedCombos: []
  },

  onLoad: function () {
    this.setData({ tableNo: App.globalData.tableNo || wx.getStorageSync('tableNo') })
    this.loadCart()
  },

  onShow: function () {
    this.loadCart()
  },

  goBack: function () {
    wx.navigateBack()
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
    const delta = parseInt(e.currentTarget.dataset.delta)
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    let cart = wx.getStorageSync('cart_' + tableNo) || []

    if (index >= cart.length) {
      return
    }

    const currentQuantity = cart[index].quantity || 1
    const newQuantity = currentQuantity + delta

    if (newQuantity < 1) {
      wx.showModal({
        title: '提示',
        content: '确定要删除该商品吗？',
        success: (res) => {
          if (res.confirm) {
            cart.splice(index, 1)
            wx.setStorageSync('cart_' + tableNo, cart)
            this.loadCart()
            wx.showToast({ title: '已删除', icon: 'success', duration: 1000 })
          }
        }
      })
      return
    }

    cart[index].quantity = newQuantity
    wx.setStorageSync('cart_' + tableNo, cart)
    this.loadCart()

    if (delta > 0) {
      wx.vibrateShort({ type: 'light' })
    }
  },

  deleteItem: function (e) {
    const index = e.currentTarget.dataset.index
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    let cart = wx.getStorageSync('cart_' + tableNo) || []

    if (index >= cart.length) {
      return
    }

    const itemName = cart[index].name
    wx.showModal({
      title: '确认删除',
      content: `确定要删除「${itemName}」吗？`,
      success: (res) => {
        if (res.confirm) {
          cart.splice(index, 1)
          wx.setStorageSync('cart_' + tableNo, cart)
          this.loadCart()
          wx.showToast({ title: '已删除', icon: 'success', duration: 1000 })
        }
      }
    })
  },

  toggleComboDetail: function (e) {
    const index = e.currentTarget.dataset.index
    const expandedCombos = [...this.data.expandedCombos]
    const idx = expandedCombos.indexOf(index)
    if (idx > -1) {
      expandedCombos.splice(idx, 1)
    } else {
      expandedCombos.push(index)
    }
    this.setData({ expandedCombos })
  },

  goOrder: function () {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    if (!this.data.tableNo) {
      wx.showToast({ title: '请先选择桌号', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/order/order' })
  },

  clearCart: function () {
    wx.showModal({
      title: '确认清空',
      content: '确定要清空购物车吗？此操作不可撤销。',
      success: (res) => {
        if (res.confirm) {
          const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
          wx.setStorageSync('cart_' + tableNo, [])
          this.setData({ items: [], total: 0, count: 0, expandedCombos: [] })
          App.globalData.cartCount = 0
          App.globalData.cartTotal = 0
          wx.showToast({ title: '已清空', icon: 'success', duration: 1000 })
        }
      }
    })
  }
})