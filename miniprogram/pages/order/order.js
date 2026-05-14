const App = getApp()

Page({
  data: {
    tableNo: '',
    items: [],
    total: 0,
    remark: '',
    expandedCombos: []
  },

  onLoad: function () {
    this.setData({ tableNo: App.globalData.tableNo || wx.getStorageSync('tableNo') })
    this.loadCart()
  },

  goBack: function () {
    wx.navigateBack()
  },

  loadCart: function () {
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    const items = wx.getStorageSync('cart_' + tableNo) || []
    let total = 0
    items.forEach(item => {
      total += item.price * item.quantity
    })
    this.setData({ items, total })
  },

  inputRemark: function (e) {
    this.setData({ remark: e.detail.value })
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

  submitOrder: function () {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }

    const orderData = {
      orderNo: 'YD' + Date.now(),
      tableNo: this.data.tableNo,
      items: this.data.items,
      remark: this.data.remark,
      totalAmount: this.data.total,
      createTime: new Date().toLocaleString('zh-CN'),
      status: 'PAID'
    }

    const orders = wx.getStorageSync('orders_' + this.data.tableNo) || []
    orders.unshift(orderData)
    wx.setStorageSync('orders_' + this.data.tableNo, orders)

    wx.setStorageSync('cart_' + this.data.tableNo, [])
    
    App.globalData.cartCount = 0
    App.globalData.cartTotal = 0

    wx.redirectTo({ 
      url: `/pages/success/success?orderNo=${orderData.orderNo}&total=${this.data.total}` 
    })
  }
})
