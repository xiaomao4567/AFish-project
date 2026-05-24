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
      { label: '已支付', value: 'PAID', count: 0 },
      { label: '制作中', value: 'PREPARING', count: 0 },
      { label: '已出餐', value: 'SERVED', count: 0 },
      { label: '已取消', value: 'CANCELLED', count: 0 }
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
    const tableNo = this.data.tableNo || wx.getStorageSync('tableNo')
    const userId = wx.getStorageSync('userId')
    
    if (!tableNo || !userId) {
      this.setData({ orders: [], allOrders: [] })
      this.updateStatusTabs([])
      return
    }

    this.setData({ loading: true })

    wx.request({
      url: 'http://127.0.0.1:8080/api/order/my',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        userId: userId,
        tableNumber: parseInt(tableNo)
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          let orders = res.data.data || []
          orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
          
          this.setData({ allOrders: orders })
          this.updateStatusTabs(orders)
          this.filterOrders()
        } else {
          this.setData({ orders: [], allOrders: [] })
          this.updateStatusTabs([])
        }
      },
      fail: (err) => {
        console.error('加载订单失败', err)
        this.setData({ orders: [], allOrders: [] })
        this.updateStatusTabs([])
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
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
    const orderNo = e.currentTarget.dataset.orderno
    const order = this.data.orders.find(o => o.orderNo === orderNo)
    
    if (!order) {
      wx.showToast({ title: '订单不存在', icon: 'none' })
      return
    }
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showModal({
            title: '取消原因',
            editable: true,
            placeholderText: '请输入取消原因',
            success: (reasonRes) => {
              if (reasonRes.confirm) {
                const cancelReason = reasonRes.content || '用户取消'
                this.doCancelOrder(order.id, cancelReason)
              }
            }
          })
        }
      }
    })
  },

  doCancelOrder: function (orderId, cancelReason) {
    wx.request({
      url: 'http://127.0.0.1:8080/api/order/' + orderId + '/cancel',
      method: 'PUT',
      header: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: { cancelReason: cancelReason },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          wx.showToast({ title: '订单已取消', icon: 'success' })
          this.hideDetailModal()
          this.loadOrders()
        } else {
          wx.showToast({ title: res.data.message || '取消失败', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('取消订单失败', err)
        wx.showToast({ title: '网络错误', icon: 'none' })
      }
    })
  },

  getStatusText: function (status) {
    const statusMap = {
      'PENDING': '待支付',
      'PAID': '已支付',
      'PREPARING': '制作中',
      'SERVED': '已上菜',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || status
  },

  getStatusClass: function (status) {
    const classMap = {
      'PENDING': 'pending',
      'PAID': 'paid',
      'PREPARING': 'preparing',
      'SERVED': 'served',
      'COMPLETED': 'completed',
      'CANCELLED': 'cancelled'
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