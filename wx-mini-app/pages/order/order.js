// pages/order/order.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    msgTip:"暂无数据",//提示消息
    user_id:"",
    orderSwiperIndex:0,//订单页的标题下标
    UserTransactionList:[],//用户交易记录
    UserRechargeList:[],//用户充值记录
  },
  //改变下标值
  changeOrderSwiperIndexOne(e){
    this.setData({
      orderSwiperIndex:0
    })
  },
  //改变下标值
  changeOrderSwiperIndexTwo(e){
    this.setData({
      orderSwiperIndex:1
    })
  },
  // swiper 切换时触发
  onSwiperChange(e) {
    const current = e.detail.current;
    // swiper 索引从0开始，我们的下标从1开始，所以要+1
    this.setData({
      orderSwiperIndex: current 
    });
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

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().updateSelected();
    }
    wx.getStorage({
      key:'isLogin',
      success:(res)=>{
        // 为true登录了
        if(res.data){
          // 获取用户id
          wx.getStorage({
            key: 'userInfo',
            success:(res)=>{
              let {userId} = res.data
              this.setData({
                user_id:userId
              })
              // 获取用户充值记录
              wx.request({
                url: 'http://localhost:8080/recharge-record/user/'+this.data.user_id,
                method:"GET",
                success:(res)=>{
                  let UserRechargeList = [...res.data.data].reverse()
                  this.setData({
                    UserRechargeList:UserRechargeList
                  })
                },
                fail:(err)=>{
                  wx.showToast({
                    title: err.data.msg,
                  })
                }
              })
              // 获取用户交易记录
              wx.request({
                url: 'http://localhost:8080/transaction/user/'+this.data.user_id,
                method:"GET",
                success:(res)=>{
                  let UserTransactionList = [...res.data.data].reverse()
                  this.setData({
                    UserTransactionList:UserTransactionList
                  })
                },
                fail:(err)=>{
                  wx.showToast({
                    title: err.data.msg,
                  })
                }
              })
            }
          })
        }else{
          wx.showToast({
            title: '请先登录',
            icon:'error'
          })
          this.setData({
            msgTip:"请先登录以获取数据"
          })
        }
      }
    })
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
