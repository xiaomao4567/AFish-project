const App = getApp()

Page({
  data: {
    username: '',
    tableNo: '',
    categories: [],
    dishes: [],
    combos: [],
    activeCategory: 0,
    searchKeyword: '',
    showCombos: false,
    loading: false,
    cartCount: 0,
    cartTotal: 0,
    showTableModal: false,
    tableList: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
    dishCount: 0,
    comboCount: 0,
    orderCount: 0
  },

  onLoad: function (options) {
    const username = wx.getStorageSync('username') || '游客'
    let tableNo = wx.getStorageSync('tableNo') || ''

    if (options && options.table) {
      tableNo = options.table
      wx.setStorageSync('tableNo', tableNo)
      App.globalData.tableNo = tableNo
    }

    this.setData({
      username: username,
      tableNo: tableNo,
      tableList: this.generateTableList()
    })

    this.loadCategoriesFromServer()
    this.loadCartCount()
    this.loadOrderCount()
  },

  onShow: function () {
    this.loadCartCount()
    this.loadOrderCount()
    this.updateTabBar()
  },

  updateTabBar: function () {
    const tabBar = this.getTabBar()
    if (tabBar) {
      tabBar.setData({ selected: 0 })
    }
  },

  generateTableList: function () {
    const list = []
    for (let i = 1; i <= 12; i++) {
      list.push(i.toString())
    }
    return list
  },

  loadCategoriesFromServer: function () {
    this.setData({ loading: true })

    wx.request({
      url: 'http://127.0.0.1:8080/api/admin/category',
      method: 'GET',
      timeout: 10000,
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          const categories = [{ id: 0, name: '全部', icon: '🍽️' }].concat(
            res.data.data.map(cat => ({
              id: cat.id,
              name: cat.name,
              icon: this.getCategoryIcon(cat.name)
            }))
          )
          categories.push({ id: -1, name: '超值套餐', icon: '🎁' })
          this.setData({ categories })
          this.loadDishesFromServer(0)
        } else {
          this.loadMockData()
        }
      },
      fail: () => {
        this.loadMockData()
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  getCategoryIcon: function (name) {
    const iconMap = {
      '招牌烤鱼': '🐟',
      '特色菜品': '🥘',
      '主食': '🍚',
      '凉菜': '🥗',
      '饮品': '🥤',
      '酒水饮料': '🍹',
      '精品凉菜': '🥢'
    }
    return iconMap[name] || '🍽️'
  },

  normalizeDish: function (dish) {
    if (Array.isArray(dish.flavors)) {
      dish.flavors = dish.flavors.join(',')
    }
    return dish
  },

  normalizeCombo: function (combo) {
    if (Array.isArray(combo.items)) {
      combo.items = combo.items.map(item => {
        if (item.flavors && Array.isArray(item.flavors)) {
          item.flavors = item.flavors.join(',')
        }
        return item
      })
    }
    return combo
  },

  loadDishesFromServer: function (categoryIndex) {
    const category = this.data.categories[categoryIndex]
    if (!category) return

    if (category.name === '超值套餐') {
      this.loadCombosFromServer()
      return
    }

    this.setData({ showCombos: false, loading: true, dishes: [], combos: [] })

    const categoryId = categoryIndex > 0 ? category.id : ''

    wx.request({
      url: 'http://127.0.0.1:8080/api/admin/dish',
      method: 'GET',
      timeout: 10000,
      data: {
        page: 1,
        size: 50,
        categoryId: categoryId
      },
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          let dishes = res.data.data.records || res.data.data
          dishes = dishes.map(dish => this.normalizeDish(dish))
          this.setData({
            dishes: dishes,
            dishCount: res.data.data.total || dishes.length
          })
        } else {
          this.loadMockDishes(categoryIndex)
        }
      },
      fail: () => {
        this.loadMockDishes(categoryIndex)
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  loadCombosFromServer: function () {
    this.setData({ showCombos: true, loading: true, dishes: [], combos: [] })

    wx.request({
      url: 'http://127.0.0.1:8080/api/admin/combo',
      method: 'GET',
      timeout: 10000,
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          let combos = res.data.data.records || res.data.data
          combos = combos.map(combo => this.normalizeCombo(combo))
          this.setData({
            combos: combos,
            showCombos: true,
            comboCount: res.data.data.total || combos.length
          })
        } else {
          this.loadMockCombos()
        }
      },
      fail: () => {
        this.loadMockCombos()
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  loadMockData: function () {
    const mockCategories = [
      { id: 0, name: '全部', icon: '🍽️' },
      { id: 2, name: '招牌烤鱼', icon: '🐟' },
      { id: 3, name: '特色菜品', icon: '🥘' },
      { id: -1, name: '超值套餐', icon: '🎁' },
      { id: 5, name: '主食', icon: '🍚' },
      { id: 6, name: '凉菜', icon: '🥗' },
      { id: 7, name: '饮品', icon: '🥤' }
    ]
    this.setData({ categories: mockCategories })
    this.loadMockDishes(0)
  },

  loadMockDishes: function (categoryIndex) {
    const category = this.data.categories[categoryIndex]
    if (!category) return

    if (category.name === '超值套餐') {
      this.loadMockCombos()
      return
    }

    this.setData({ showCombos: false, dishes: [], combos: [] })

    const mockDishes = [
      {
        id: 1,
        name: '麻辣烤鱼',
        description: '精选鲜活鲈鱼，秘制麻辣酱料',
        price: 88,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=spicy%20grilled%20fish%20with%20red%20chili%20peppers%20and%20vegetables%20on%20plate%20food%20photography&image_size=landscape_4_3',
        flavors: '麻辣,微辣,酱香',
        categoryId: 2,
        status: 1
      },
      {
        id: 2,
        name: '蒜香烤鱼',
        description: '蒜香浓郁，回味无穷',
        price: 88,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=garlic%20grilled%20fish%20with%20fresh%20herbs%20on%20plate%20food%20photography&image_size=landscape_4_3',
        flavors: '蒜香,原味',
        categoryId: 2,
        status: 1
      },
      {
        id: 3,
        name: '酸菜烤鱼',
        description: '酸爽开胃，鱼肉鲜嫩',
        price: 88,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sour%20vegetable%20grilled%20fish%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '酸菜',
        categoryId: 2,
        status: 1
      },
      {
        id: 4,
        name: '香辣虾',
        description: '精选大虾，香辣可口',
        price: 68,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=spicy%20garlic%20shrimp%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '香辣',
        categoryId: 3,
        status: 1
      },
      {
        id: 5,
        name: '干锅牛蛙',
        description: '鲜嫩牛蛙，麻辣鲜香',
        price: 78,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=spicy%20dry%20pot%20frog%20legs%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '麻辣,微辣',
        categoryId: 3,
        status: 1
      },
      {
        id: 6,
        name: '酸辣土豆丝',
        description: '经典凉菜，酸辣爽口',
        price: 18,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=shredded%20potato%20salad%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '酸辣',
        categoryId: 6,
        status: 1
      },
      {
        id: 7,
        name: '凉拌木耳',
        description: '清爽可口，营养健康',
        price: 16,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=cold%20wood%20ear%20mushroom%20salad%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '原味',
        categoryId: 6,
        status: 1
      },
      {
        id: 8,
        name: '蛋炒饭',
        description: '粒粒分明，蛋香浓郁',
        price: 22,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=egg%20fried%20rice%20chinese%20style%20food%20photography&image_size=landscape_4_3',
        flavors: '原味',
        categoryId: 5,
        status: 1
      },
      {
        id: 9,
        name: '白米饭',
        description: '五常大米，软糯香甜',
        price: 3,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=white%20rice%20in%20bowl%20food%20photography&image_size=landscape_4_3',
        flavors: '原味',
        categoryId: 5,
        status: 1
      },
      {
        id: 10,
        name: '酸梅汤',
        description: '酸甜可口，解暑佳品',
        price: 12,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sour%20plum%20drink%20chinese%20style%20beverage%20photography&image_size=landscape_4_3',
        flavors: '原味',
        categoryId: 7,
        status: 1
      },
      {
        id: 11,
        name: '鲜榨橙汁',
        description: '新鲜橙子，现榨现卖',
        price: 18,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=fresh%20orange%20juice%20in%20glass%20beverage%20photography&image_size=landscape_4_3',
        flavors: '原味',
        categoryId: 7,
        status: 1
      },
      {
        id: 12,
        name: '水煮牛肉',
        description: '麻辣鲜香，嫩滑可口',
        price: 58,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=sichuan%20boiled%20beef%20in%20chili%20oil%20chinese%20food%20photography&image_size=landscape_4_3',
        flavors: '麻辣,微辣',
        categoryId: 3,
        status: 1
      }
    ]

    let filteredDishes = mockDishes
    if (categoryIndex > 0 && category.id > 0) {
      filteredDishes = mockDishes.filter(d => d.categoryId === category.id)
    }

    if (this.data.searchKeyword) {
      const keyword = this.data.searchKeyword.toLowerCase()
      filteredDishes = filteredDishes.filter(d =>
        d.name.toLowerCase().includes(keyword) ||
        d.description.toLowerCase().includes(keyword)
      )
    }

    this.setData({
      dishes: filteredDishes,
      dishCount: mockDishes.length,
      loading: false
    })
  },

  loadMockCombos: function () {
    this.setData({ showCombos: true, dishes: [], combos: [] })
    
    const mockCombos = [
      {
        id: 1,
        name: '双人烤鱼套餐',
        description: '精选烤鱼一份 + 配菜 + 饮品',
        price: 168,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=two%20person%20grilled%20fish%20meal%20set%20with%20side%20dishes%20food%20photography&image_size=landscape_4_3',
        status: 1,
        items: [
          { dishId: 1, dishName: '麻辣烤鱼', flavor: '麻辣', quantity: 1, price: 88 },
          { dishId: 6, dishName: '酸辣土豆丝', flavor: '原味', quantity: 1, price: 18 },
          { dishId: 10, dishName: '酸梅汤', flavor: '原味', quantity: 2, price: 12 }
        ]
      },
      {
        id: 2,
        name: '四人欢聚套餐',
        description: '双份烤鱼 + 配菜 + 主食 + 饮品',
        price: 328,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=four%20person%20grilled%20fish%20feast%20meal%20set%20chinese%20restaurant%20food%20photography&image_size=landscape_4_3',
        status: 1,
        items: [
          { dishId: 1, dishName: '麻辣烤鱼', flavor: '麻辣', quantity: 1, price: 88 },
          { dishId: 2, dishName: '蒜香烤鱼', flavor: '蒜香', quantity: 1, price: 88 },
          { dishId: 6, dishName: '酸辣土豆丝', flavor: '原味', quantity: 1, price: 18 },
          { dishId: 7, dishName: '凉拌木耳', flavor: '原味', quantity: 1, price: 16 },
          { dishId: 8, dishName: '蛋炒饭', flavor: '原味', quantity: 2, price: 22 },
          { dishId: 10, dishName: '酸梅汤', flavor: '原味', quantity: 4, price: 12 }
        ]
      },
      {
        id: 3,
        name: '家庭欢乐套餐',
        description: '烤鱼 + 特色菜 + 主食 + 饮品',
        price: 268,
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=family%20dinner%20set%20with%20grilled%20fish%20and%20various%20dishes%20food%20photography&image_size=landscape_4_3',
        status: 1,
        items: [
          { dishId: 3, dishName: '酸菜烤鱼', flavor: '酸菜', quantity: 1, price: 88 },
          { dishId: 4, dishName: '香辣虾', flavor: '香辣', quantity: 1, price: 68 },
          { dishId: 8, dishName: '蛋炒饭', flavor: '原味', quantity: 3, price: 22 },
          { dishId: 11, dishName: '鲜榨橙汁', flavor: '原味', quantity: 3, price: 18 }
        ]
      }
    ]

    this.setData({
      combos: mockCombos,
      showCombos: true,
      comboCount: mockCombos.length,
      loading: false
    })
  },

  switchCategory: function (e) {
    const index = parseInt(e.currentTarget.dataset.index)
    this.setData({ activeCategory: index })

    const category = this.data.categories[index]
    if (category && category.name === '超值套餐') {
      this.loadCombosFromServer()
    } else {
      this.loadDishesFromServer(index)
    }
  },

  search: function (e) {
    const keyword = e.detail.value
    this.setData({ searchKeyword: keyword })

    const category = this.data.categories[this.data.activeCategory]
    if (category && category.name === '超值套餐') {
      this.loadCombosFromServer()
    } else {
      this.loadDishesFromServer(this.data.activeCategory)
    }
  },

  goDetail: function (e) {
    const item = e.currentTarget.dataset.item
    const isCombo = e.currentTarget.dataset.isCombo === 'true'

    wx.navigateTo({
      url: `/pages/detail/detail?item=${encodeURIComponent(JSON.stringify(item))}&isCombo=${isCombo}`
    })
  },

  goCart: function () {
    wx.navigateTo({ url: '/pages/cart/cart' })
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
    const oldTableNo = this.data.tableNo
    
    if (oldTableNo && oldTableNo !== tableNo) {
      wx.showModal({
        title: '切换桌号',
        content: `是否清空当前桌号 ${oldTableNo} 的购物车？`,
        success: (res) => {
          if (res.confirm) {
            this.confirmTableSwitch(tableNo, true)
          } else if (res.cancel) {
            this.confirmTableSwitch(tableNo, false)
          }
        }
      })
    } else {
      this.confirmTableSwitch(tableNo, false)
    }
  },

  confirmTableSwitch: function (tableNo, clearCart) {
    if (clearCart) {
      const oldTableNo = this.data.tableNo
      wx.removeStorageSync('cart_' + oldTableNo)
      App.globalData.cartCount = 0
      App.globalData.cartTotal = 0
    }
    
    this.setData({ tableNo: tableNo, showTableModal: false })
    App.globalData.tableNo = tableNo
    wx.setStorageSync('tableNo', tableNo)
    
    this.loadCartCount()
    
    wx.showToast({ title: `已切换至 ${tableNo}号桌`, icon: 'success' })
  },

  loadCartCount: function () {
    const tableNo = this.data.tableNo
    const cart = wx.getStorageSync('cart_' + tableNo) || []
    const count = cart.reduce((sum, item) => sum + item.quantity, 0)
    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0)
    this.setData({ cartCount: count, cartTotal: total })
    App.globalData.cartCount = count
    App.globalData.cartTotal = total
  },

  loadOrderCount: function () {
    const tableNo = this.data.tableNo
    if (!tableNo) {
      this.setData({ orderCount: 0 })
      return
    }
    const orders = wx.getStorageSync('orders_' + tableNo) || []
    this.setData({ orderCount: orders.length })
  },

  logout: function () {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('username')
          wx.removeStorageSync('tableNo')
          wx.removeStorageSync('cart_' + this.data.tableNo)
          App.globalData.tableNo = ''
          wx.switchTab({ url: '/pages/index/index' })
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/login' })
          }, 500)
        }
      }
    })
  }
})