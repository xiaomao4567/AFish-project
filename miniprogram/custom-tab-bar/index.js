Component({
  data: {
    selected: 0,
    color: '#999999',
    selectedColor: '#ff6b35',
    list: [
      {
        pagePath: '/pages/index/index',
        text: '首页',
        icon: '🏠',
        activeIcon: '🏠'
      },
      {
        pagePath: '/pages/orders/orders',
        text: '订单',
        icon: '📋',
        activeIcon: '📋'
      }
    ]
  },

  attached: function() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const currentPagePath = '/' + currentPage.route
    
    const index = this.data.list.findIndex(item => item.pagePath === currentPagePath)
    if (index !== -1) {
      this.setData({ selected: index })
    }
  },

  methods: {
    switchTab: function(e) {
      const data = e.currentTarget.dataset
      const url = data.path
      
      if (url !== '/' + getCurrentPages()[getCurrentPages().length - 1].route) {
        wx.switchTab({ url })
      }
    }
  }
})