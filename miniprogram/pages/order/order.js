const App = getApp()

Page({
  data: {
    tableNo: '',
    items: [],
    total: 0,
    remark: '',
    expandedCombos: [],
    submitting: false
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

    if (this.data.submitting) return

    this.setData({ submitting: true })

    const userId = wx.getStorageSync('userId')
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      this.setData({ submitting: false })
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }

    const orderItems = this.data.items.map(item => ({
      dishId: item.dishId,
      dishName: item.dishName || item.name,
      image: item.image,
      flavor: item.flavor,
      quantity: item.quantity,
      price: item.price,
      isCombo: item.isCombo || false
    }))

    wx.request({
      url: 'http://127.0.0.1:8080/api/order/create',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      data: {
        tableNumber: parseInt(this.data.tableNo),
        remark: this.data.remark,
        userId: userId,
        items: orderItems
      },
      success: (res) => {
        if (res.data.code === 200) {
          const tableNo = this.data.tableNo
          wx.setStorageSync('cart_' + tableNo, [])
          
          App.globalData.cartCount = 0
          App.globalData.cartTotal = 0

          wx.redirectTo({ 
            url: `/pages/success/success?orderNo=${res.data.data.orderNo}&total=${this.data.total}` 
          })
        } else {
          wx.showToast({ title: res.data.message || '下单失败', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('下单失败', err)
        wx.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
      },
      complete: () => {
        this.setData({ submitting: false })
      }
    })
  }
})
