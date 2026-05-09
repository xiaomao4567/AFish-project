const App = getApp()

Page({
  data: {
    tableNo: '',
    orders: [],
    loading: false,
    showTableModal: false,
    tableList: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
  },

  onLoad: function () {
    this.setData({ tableNo: wx.getStorageSync('tableNo') })
    this.loadOrders()
  },

  onShow: function () {
    this.loadOrders()
  },

  loadOrders: function () {
    const tableNo = this.data.tableNo
    if (!tableNo) {
      this.setData({ orders: [] })
      return
    }

    const orders = wx.getStorageSync('orders_' + tableNo) || []
    this.setData({ orders })
  },

  viewDetail: function (e) {
    const orderNo = e.currentTarget.dataset.orderNo
    const order = this.data.orders.find(o => o.orderNo === orderNo)
    if (order) {
      let detail = `订单号：${order.orderNo}\n时间：${order.createTime}\n状态：${this.getStatusText(order.status)}\n金额：¥${order.totalAmount}`
      if (order.remark) {
        detail += `\n备注：${order.remark}`
      }
      wx.showModal({
        title: '订单详情',
        content: detail,
        showCancel: false
      })
    }
  },

  refreshOrders: function () {
    this.loadOrders()
  },

  getStatusText: function (status) {
    const statusMap = {
      'PENDING': '待支付',
      'PAID': '已支付',
      'PREPARING': '制作中',
      'SERVED': '已上菜',
      'COMPLETED': '已完成'
    }
    return statusMap[status] || status
  },

  getStatusClass: function (status) {
    const classMap = {
      'PENDING': 'pending',
      'PAID': 'paid',
      'PREPARING': 'preparing',
      'SERVED': 'served',
      'COMPLETED': 'completed'
    }
    return classMap[status] || 'pending'
  },

  showTableModal: function () {
    this.setData({ showTableModal: true })
  },

  hideTableModal: function () {
    this.setData({ showTableModal: false })
  },

  selectTable: function (e) {
    const tableNo = e.currentTarget.dataset.table
    this.setData({ tableNo: tableNo, showTableModal: false })
    App.globalData.tableNo = tableNo
    wx.setStorageSync('tableNo', tableNo)
    wx.showToast({ title: `已选择桌号 ${tableNo}`, icon: 'success' })
    this.loadOrders()
  }
})
