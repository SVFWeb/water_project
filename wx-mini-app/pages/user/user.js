// pages/user/user.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    user_name:"暂未登录",
    user_id:"……",
    is_login:false,
  },
  // 获取openid登录
  userLogin(e) {
    wx.showLoading({
      title: '登陆中',
    })
    wx.login({
      success: (res) => {
        if (res.code) {
          wx.request({
            url: 'https://api.weixin.qq.com/sns/jscode2session',
            method: 'GET',
            data: {
              appid: 'wx8330e635b27ec4d1', 
              secret: '7ddb4ae35beeaa5c5bdb49eeb24a40f1',
              js_code: res.code,
              grant_type: 'authorization_code'
            },
            success: (res) => {
              console.log('获取 openid 和 session_key', res.data)
              let openid = res.data.openid
              wx.request({
                url: 'http://localhost:8080/user/by-openid',
                method: 'POST',
                data: {
                  openId:openid
                },
                success:(res)=>{
                  console.log('发送了openid返回', res.data)
                  wx.hideLoading();
                  wx.showToast({
                    title: '登陆成功',
                  })
                  wx.setStorage({
                    key:"userInfo",
                    data:res.data.data.user
                  })
                  wx.setStorage({
                    key:"isLogin",
                    data:true
                  })
                  let {userName,userId} = res.data.data.user;
                  this.setData({
                    is_login:true,
                    user_name:userName,
                    user_id:userId,
                  })
                },
                fail: (err) => {
                  console.error('请求失败', err)
                }
              })
            },
            fail: (err) => {
              console.error('请求失败', err)
            }
          })
        }
      }
    })
  },
  // 退出登录
  userExitLogin(e){
    if(this.data.is_login){
      wx.showLoading({
        title:"退出中"
      })
      this.setData({
        is_login:false,
      })
      wx.clearStorage({
        key:"userInfo"
      })
      wx.setStorage({
        key:"isLogin",
        data:false
      })
      wx.hideLoading();
      wx.showToast({
        title: '退出成功',
      })
    }else{
      wx.showToast({
        title: '您还未登录',
        icon:"error"
      })
    }
  },
  // 跳转到交易记录页面
  GoToOrders(e){
    wx.switchTab({
      url: '../order/order',
    })
  },
  // 跳转到充值记录
  GoToOrdersTopUp(e){
    wx.switchTab({
      url: '../order/order',
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
 
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
   // 更新用户数据
   wx.getStorage({
    key: 'userInfo',
    success:(res)=>{
      const {userId,userName} = res.data
      this.setData({
        user_name:userName,
        user_id:userId,
        is_login:true
      })
    }
  })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().updateSelected();
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})
