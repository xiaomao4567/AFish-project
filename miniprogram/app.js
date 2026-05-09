App({
  onLaunch: function (options) {
    console.log('App Launch', options)
    if (options && options.query && options.query.table) {
      this.globalData.tableNo = options.query.table
      wx.setStorageSync('tableNo', options.query.table)
    }
  },

  onShow: function (options) {
    console.log('App Show', options)
    if (options && options.query && options.query.table) {
      this.globalData.tableNo = options.query.table
      wx.setStorageSync('tableNo', options.query.table)
    }
  },

  onHide: function () {
    console.log('App Hide')
  },

  globalData: {
    tableNo: '',
    cartCount: 0,
    cartTotal: 0,
    isLoggedIn: false
  },

  checkLogin: function () {
    const token = wx.getStorageSync('token')
    return !!token
  },

  logout: function () {
    wx.removeStorageSync('token')
    wx.removeStorageSync('username')
    wx.removeStorageSync('tableNo')
    this.globalData.isLoggedIn = false
    this.globalData.tableNo = ''
  }
})
