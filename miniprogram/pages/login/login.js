const App = getApp()

Page({
  data: {
    loading: false
  },

  onLoad: function () {
    this.checkAutoLogin()
  },

  checkAutoLogin: function () {
    const token = wx.getStorageSync('token')
    const tokenExpireTime = wx.getStorageSync('tokenExpireTime')
    
    if (token && tokenExpireTime) {
      const now = Date.now()
      if (now < tokenExpireTime) {
        wx.switchTab({ url: '/pages/index/index' })
        return
      } else {
        wx.removeStorageSync('token')
        wx.removeStorageSync('tokenExpireTime')
        wx.removeStorageSync('username')
        wx.removeStorageSync('userId')
      }
    }
  },

  wechatLogin: function () {
    this.setData({ loading: true })
    
    wx.login({
      success: (loginRes) => {
        console.log('wx.login success:', loginRes)
        
        if (loginRes.code) {
          this.sendLoginRequest(loginRes.code)
        } else {
          console.error('wx.login failed, no code')
          this.guestLogin()
        }
      },
      fail: (err) => {
        console.error('wx.login error:', err)
        this.guestLogin()
      }
    })
  },

  sendLoginRequest: function (code) {
    console.log('Sending login request with code:', code)
    
    wx.request({
      url: 'http://127.0.0.1:8080/api/auth/wechat-login',
      method: 'POST',
      timeout: 10000,
      data: { 
        code: code,
        nickName: '微信用户',
        avatarUrl: ''
      },
      success: (response) => {
        console.log('Server response:', response)
        
        if (response.statusCode === 200 && response.data && response.data.code === 200) {
          wx.setStorageSync('token', response.data.data.token)
          wx.setStorageSync('username', response.data.data.username || '微信用户')
          wx.setStorageSync('userId', response.data.data.id)
          wx.setStorageSync('tokenExpireTime', Date.now() + 3600000)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' })
          }, 1500)
        } else {
          console.error('Login failed, response:', response)
          wx.showToast({ title: '登录失败，使用游客模式', icon: 'none' })
          setTimeout(() => {
            this.guestLogin()
          }, 1000)
        }
      },
      fail: (err) => {
        console.error('Request failed:', err)
        wx.showToast({ title: '网络错误，使用游客模式', icon: 'none' })
        setTimeout(() => {
          this.guestLogin()
        }, 1000)
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  },

  guestLogin: function () {
    wx.setStorageSync('token', 'guest_token')
    wx.setStorageSync('username', '游客')
    wx.setStorageSync('tokenExpireTime', Date.now() + 3600000)
    wx.showToast({ title: '游客登录成功', icon: 'success' })
    setTimeout(() => {
      wx.switchTab({ url: '/pages/index/index' })
    }, 1500)
  }
})