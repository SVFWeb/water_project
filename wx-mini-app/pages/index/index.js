
Page({
  data: {
    city: '南宁市', // 默认城市
    balance:1.23,
    SwiperIndex:1
  },
  onLoad: function () {
    this.getCurrentCity();
  },
  getCurrentCity: function () {
    // 首先检查用户是否已经授权位置信息
    wx.getSetting({
      success: (res) => {
        if (res.authSetting['scope.userLocation']) {
          // 已经授权，可以直接调用 wx.getLocation
          this.getLocationAndReverseGeocoder();
        } else {
          // 未授权，请求授权
          wx.authorize({
            scope: 'scope.userLocation',
            success: () => {
              // 用户同意授权
              this.getLocationAndReverseGeocoder();
            },
            fail: () => {
              // 用户拒绝授权，使用默认城市或引导用户手动选择
              this.setData({
                city: '默认城市'
              });
              // 可以在这里提示用户手动选择城市，或者使用默认城市
              console.log('用户拒绝授权');
            }
          });
        }
      }
    });
  },
  getLocationAndReverseGeocoder: function() {
    
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().updateSelected();
    }
  }
});