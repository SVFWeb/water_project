// pages/order/order.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    orderSwiperIndex:1,//订单页的标题下标
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
