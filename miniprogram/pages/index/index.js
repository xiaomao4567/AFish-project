const App = getApp()
const { mockCategories, mockDishes, mockCombos } = require('../../utils/mockData.js')

Page({
  data: {
    tableNo: '',
    categories: [],
    activeCategory: 0,
    searchKeyword: '',
    dishes: [],
    combos: [],
    showCombos: false,
    cartCount: 0,
    cartTotal: 0,
    username: '',
    showTableModal: false,
    tableList: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
    loading: false,
    useMockData: true
  },

  onLoad: function (options) {
    console.log('首页加载中...')
    this.checkLoginStatus()
    if (options && options.table) {
      this.setData({ tableNo: options.table })
      App.globalData.tableNo = options.table
      wx.setStorageSync('tableNo', options.table)
    } else {
      const storedTable = wx.getStorageSync('tableNo')
      if (storedTable) {
        this.setData({ tableNo: storedTable })
        App.globalData.tableNo = storedTable
      }
    }
    const username = wx.getStorageSync('username') || '用户'
    this.setData({ username })
    console.log('用户名:', username)
    this.loadCategories()
  },

  onShow: function () {
    this.checkLoginStatus()
    if (!this.data.tableNo) {
      const storedTable = wx.getStorageSync('tableNo')
      if (storedTable) {
        this.setData({ tableNo: storedTable })
        App.globalData.tableNo = storedTable
      }
    }
  },

  checkLoginStatus: function () {
    const token = wx.getStorageSync('token')
    console.log('Token:', token ? '已存在' : '不存在')
    if (!token) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    App.globalData.isLoggedIn = true
  },

  loadCategories: function () {
    console.log('加载分类...')
    const categories = [{ id: 0, name: '全部', icon: '🍽️' }]
    mockCategories.forEach(cat => {
      categories.push({ id: cat.id, name: cat.name, icon: cat.icon })
    })
    categories.push({ id: -1, name: '超值套餐', icon: '🎁' })
    this.setData({ categories, useMockData: true })
    console.log('分类加载完成:', categories.length)
    this.loadDishes()
  },

  loadDishes: function () {
    const categoryId = this.data.categories[this.data.activeCategory]?.id
    console.log('加载菜品, categoryId:', categoryId)
    
    if (categoryId === -1) {
      this.setData({ combos: mockCombos, showCombos: true })
      console.log('加载套餐:', mockCombos.length)
    } else if (categoryId === 0) {
      this.setData({ dishes: mockDishes, showCombos: false })
      console.log('加载全部菜品:', mockDishes.length)
    } else {
      const filtered = mockDishes.filter(d => d.categoryId === categoryId)
      this.setData({ dishes: filtered, showCombos: false })
      console.log('加载分类菜品:', filtered.length)
    }
  },

  switchCategory: function (e) {
    const index = e.currentTarget.dataset.index
    this.setData({ activeCategory: index })
    this.loadDishes()
  },

  search: function (e) {
    const keyword = e.detail.value
    this.setData({ searchKeyword: keyword })
    this.filterDishes(keyword)
  },

  filterDishes: function (keyword) {
    if (!keyword) {
      this.loadDishes()
      return
    }
    
    const filtered = mockDishes.filter(d => d.name.includes(keyword))
    this.setData({ dishes: filtered, showCombos: false })
  },

  goDetail: function (e) {
    const item = e.currentTarget.dataset.item
    const isCombo = e.currentTarget.dataset.isCombo
    wx.navigateTo({
      url: `/pages/detail/detail?id=${item.id}&isCombo=${isCombo}`
    })
  },

  goCart: function () {
    if (!this.data.tableNo) {
      this.setData({ showTableModal: true })
      return
    }
    wx.navigateTo({ url: '/pages/cart/cart' })
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
  },

  logout: function () {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          App.logout()
          wx.redirectTo({ url: '/pages/login/login' })
        }
      }
    })
  },

  stopPropagation: function () {
  }
})
