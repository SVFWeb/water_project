// pages/machine/machine.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    machine_status:"离线",//设备在线情况
    user_id:"",//用户id
    machineNum:"001",//机器序号
    rate_sum:0.27,//加水价格
    price_per_liter:0.004,//水费
    service_fee:0.266,//服务费
    userBalance:0,//用户余额
    water_yield:0,//可加水升数
    machine_id:"0",//机器编号
    device_temperature:0,//设备温度
    battery_level:0,//电池电量
    is_connect:0,//已连接设备？
  },
  // 返回上一页
  backTab(){
    wx.navigateBack({
      delta: 1
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
     // 获取本地缓存
     wx.getStorage({
      key: 'userInfo',
      success:(res)=>{
        console.log(res.data)
        const {userId,balance} = res.data
        this.setData({
          user_id:userId,
          userBalance:balance,
          water_yield:(balance/this.data.rate_sum).toFixed(2)
        })
      }
    })
    // 获取机器信息
    wx.request({
      url: 'http://localhost:8080/machine/ma1',
      method:"GET",
      success:(res)=>{
        console.log(res.data);
        const {status,machineId,deviceTemperature,batteryLevel} = res.data.data;
        this.setData({
          machine_status:status,//设备在线情况
          machine_id:machineId,//机器编号
          device_temperature:deviceTemperature,//设备温度
          battery_level:batteryLevel,//电池电量
        })
      },
      fail:(res)=>{
        console.log(res.data);
      }
    })

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

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