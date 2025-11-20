
Page({
  data: {
    city: '南宁市', // 默认城市
    balance:0.00,
    SwiperIndex:0
  },
    //改变下标值
    changeSwiperIndexOne(e){
      this.setData({
        SwiperIndex:0
      })
    },
    //改变下标值
    changeSwiperIndexTwo(e){
      this.setData({
        SwiperIndex:1
      })
    },
    // swiper 切换时触发
    onSwiperChange(e) {
      const current = e.detail.current;
      // swiper 索引从0开始，我们的下标从1开始，所以要+1
      this.setData({
        SwiperIndex: current 
      });
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
  // 页面进来一次就触发
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().updateSelected();
    }
    wx.getStorage({
      key: 'isLogin',
      success:(res)=>{
        if(!res.data){
          this.setData({
            balance:0.00,
          })
        }else{
          // 获取本地缓存
          wx.getStorage({
            key: 'userInfo',
            success:(res)=>{
              const {balance} = res.data
              this.setData({
                balance:balance
              })
            }
        })
        }
      }
  })
  
  },
   /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
   // 获取本地缓存
    wx.getStorage({
      key: 'userInfo',
      success:(res)=>{
        console.log(res.data)
        const {balance} = res.data
        this.setData({
          balance:balance
        })
      }
    })
   },
});