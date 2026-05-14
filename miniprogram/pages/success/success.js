Page({
  data: {
    orderNo: '',
    total: 0,
    totalStr: '0.00',
    status: '已支付',
    createTime: ''
  },

  onLoad: function (options) {
    const total = parseFloat(options.total) || 0
    this.setData({
      orderNo: options.orderNo,
      total: total,
      totalStr: total.toFixed(2),
      createTime: this.formatTime(new Date())
    })
  },

  goBack: function () {
    wx.switchTab({ url: '/pages/index/index' })
  },

  formatTime: function (date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  goHome: function () {
    wx.switchTab({ url: '/pages/index/index' })
  },

  viewOrder: function () {
    wx.switchTab({ url: '/pages/orders/orders' })
  }
})