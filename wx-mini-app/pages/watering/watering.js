// pages/watering/watering.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 纬度
    latitude:23.188121,
    // 经度
    longitude:108.147359,
    posname:"学校",
    posaddress:"109-2教室",
    // 默认
    markers:[{
    latitude:23.188121,
    longitude:108.147359,
    id:0,
    // 图标路径
    iconPath:"",
    width:30,
    height:40,
    }]
  },
    // 点击标记点
    onMarkerTap: function(e) {
      const markerId = e.markerId
      const marker = this.data.markers.find(item => item.id === markerId)
      
      if (marker) {
        wx.showActionSheet({
          itemList: ['微信内置地图导航'],
          success: (res) => {
            if (res.tapIndex === 0) {
              // 使用微信内置地图
              wx.openLocation({
                latitude: marker.latitude,
                longitude: marker.longitude,
                name: marker.title || '设备位置',
                scale: 18
              })
            } else if (res.tapIndex === 1) {
              // 复制坐标
              wx.setClipboardData({
                data: `${marker.latitude},${marker.longitude}`,
                success: () => {
                  wx.showToast({
                    title: '坐标已复制',
                    icon: 'success'
                  })
                }
              })
            }
          }
        })
      }
    },
// 跳转到机器页面
  GoToMachine(e){
    wx.navigateTo({
      url: '../machine/machine',
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
