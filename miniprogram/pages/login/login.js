Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  inputUsername: function (e) {
    this.setData({ username: e.detail.value })
  },

  inputPassword: function (e) {
    this.setData({ password: e.detail.value })
  },

  login: function () {
    const { username, password } = this.data
    
    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return
    }
    
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }

    this.setData({ loading: true })

    wx.request({
      url: 'http://127.0.0.1:8080/api/auth/login',
      method: 'POST',
      timeout: 3000,
      data: {
        username: username,
        password: password
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          wx.setStorageSync('token', res.data.data.token)
          wx.setStorageSync('username', username)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' })
          }, 1500)
        } else {
          wx.showToast({ title: '用户名或密码错误', icon: 'none' })
        }
      },
      fail: (err) => {
        console.error('登录请求失败，使用离线模式:', err)
        wx.setStorageSync('token', 'offline_token')
        wx.setStorageSync('username', username)
        wx.showToast({ title: '离线模式登录', icon: 'success' })
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' })
        }, 1500)
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  guestLogin: function () {
    wx.setStorageSync('token', 'guest_token')
    wx.setStorageSync('username', '游客')
    wx.showToast({ title: '游客登录成功', icon: 'success' })
    setTimeout(() => {
      wx.switchTab({ url: '/pages/index/index' })
    }, 1500)
  }
})
