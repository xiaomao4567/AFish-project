const App = getApp()

Page({
  data: {
    tableNo: '',
    orders: [],
    allOrders: [],
    loading: false,
    showTableModal: false,
    showDetailModal: false,
    currentOrder: null,
    tableList: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
    currentStatus: 'ALL',
    statusTabs: [
      { label: '全部', value: 'ALL', count: 0 },
      { label: '待支付', value: 'PENDING', count: 0 },
      { label: '已支付', value: 'PAID', count: 0 },
      { label: '制作中', value: 'PREPARING', count: 0 },
      { label: '已上菜', value: 'SERVED', count: 0 }
    ]
  },

  onLoad: function () {
    this.setData({ tableNo: wx.getStorageSync('tableNo') })
    this.loadOrders()
  },

  onShow: function () {
    this.loadOrders()
    this.updateTabBar()
  },

  updateTabBar: function () {
    const tabBar = this.getTabBar()
    if (tabBar) {
      tabBar.setData({ selected: 1 })
    }
  },

  goBack: function () {
    wx.switchTab({ url: '/pages/index/index' })
  },

  loadOrders: function () {
    const tableNo = this.data.tableNo
    if (!tableNo) {
      this.setData({ orders: [], allOrders: [] })
      this.updateStatusTabs([])
      return
    }

    const orders = wx.getStorageSync('orders_' + tableNo) || []
    orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    
    this.setData({ allOrders: orders })
    this.updateStatusTabs(orders)
    this.filterOrders()
  },

  updateStatusTabs: function (orders) {
    const statusTabs = this.data.statusTabs.map(tab => {
      if (tab.value === 'ALL') {
        return { ...tab, count: orders.length }
      }
      return { ...tab, count: orders.filter(o => o.status === tab.value).length }
    })
    this.setData({ statusTabs })
  },

  switchStatus: function (e) {
    const status = e.currentTarget.dataset.status
    this.setData({ currentStatus: status })
    this.filterOrders()
  },

  filterOrders: function () {
    const { allOrders, currentStatus } = this.data
    if (currentStatus === 'ALL') {
      this.setData({ orders: allOrders })
    } else {
      this.setData({ orders: allOrders.filter(o => o.status === currentStatus) })
    }
  },

  viewDetail: function (e) {
    const orderNo = e.currentTarget.dataset.orderNo
    const order = this.data.orders.find(o => o.orderNo === orderNo)
    if (order) {
      this.setData({ currentOrder: order, showDetailModal: true })
    }
  },

  hideDetailModal: function () {
    this.setData({ showDetailModal: false, currentOrder: null })
  },

  refreshOrders: function () {
    this.loadOrders()
  },

  payOrder: function (e) {
    const orderNo = e.currentTarget.dataset.orderNo
    const orders = this.data.orders
    const index = orders.findIndex(o => o.orderNo === orderNo)
    
    if (index > -1) {
      orders[index].status = 'PAID'
      orders[index].payTime = new Date().toLocaleString('zh-CN')
      wx.setStorageSync('orders_' + this.data.tableNo, orders)
      this.setData({ orders, currentOrder: orders[index] })
      
      wx.showModal({
        title: '支付成功',
        content: '订单已支付，正在准备菜品',
        showCancel: false,
        success: () => {
          this.hideDetailModal()
        }
      })
    }
  },

  cancelOrder: function (e) {
    const orderNo = e.currentTarget.dataset.orderNo
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          let orders = this.data.orders
          orders = orders.filter(o => o.orderNo !== orderNo)
          wx.setStorageSync('orders_' + this.data.tableNo, orders)
          this.setData({ orders })
          this.hideDetailModal()
          wx.showToast({ title: '订单已取消', icon: 'success' })
        }
      }
    })
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

  stopPropagation: function () {},

  selectTable: function (e) {
    const tableNo = e.currentTarget.dataset.table
    this.setData({ tableNo: tableNo, showTableModal: false })
    App.globalData.tableNo = tableNo
    wx.setStorageSync('tableNo', tableNo)
    wx.showToast({ title: `已选择桌号 ${tableNo}`, icon: 'success' })
    this.loadOrders()
  }
})