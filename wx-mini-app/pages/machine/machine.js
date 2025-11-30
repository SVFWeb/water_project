
const wxp = require('../../utils/wxp');
Page({
  data: {
    // 状态管理
    is_pause: 0,
    is_watering: 0,
    showLoginModal: false,
    showBalanceModal: false,
    isLoading: false,
    is_connect: 0,
    // 基础机器和水费参数
    rate_sum: 0.27,
    price_per_liter: 0.004,
    service_fee: 0.266,
    machineNum: "001",
    // 用户余额，id
    user_id: "",
    userBalance: 0,
    //可以加水的升数
    water_yield: 0,
    // 机器属性
    machine_status: "离线",
    machine_id: "0",
    device_temperature: 0,
    battery_level: 0,
    total_water_addition: 0,
  },
  // 返回上一页
  backTab() {
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer() // 停止设备信息查询
    wx.navigateBack({
      delta: 1
    });
  },
  // 显示登录提示弹窗
  showLoginModal() {
    this.setData({
      showLoginModal: true
    })
  },
  // 显示余额不足弹窗
  showBalanceModal() {
    this.setData({
      showBalanceModal: true
    })
  },
  // 关闭登录弹窗
  onCloseLoginModal() {
    this.setData({
      showLoginModal: false
    })
  },
  // 关闭余额不足弹窗
  onCloseBalanceModal() {
    this.setData({
      showBalanceModal: false
    })
  },
  // 确认去登录
  onConfirmLogin() {
    this.setData({
      showLoginModal: false
    })
    wx.showModal({
      title: '前往登录',
      content: '请切换到"我的"页面进行登录',
      showCancel: false,
      confirmText: '知道了',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '请切换到"我的"页面',
            icon: 'none',
            duration: 2000
          })
          wx.navigateBack({})
        }
      }
    })
  },
  // 确认去充值
  onConfirmRecharge() {
    this.setData({
      showBalanceModal: false
    })
    wx.showModal({
      title: '余额不足',
      content: '请先充值后再使用设备',
      showCancel: false,
      confirmText: '知道了',
      success: (res) => {
        if (res.confirm) {
          wx.showToast({
            title: '请先充值',
            icon: 'none',
            duration: 2000
          })
          wx.navigateBack({})
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 可以在这里初始化一些数据
    const machineData= this.selectMachine();
    this.setData({
      machine_status: machineData.status,
      machine_id: machineData.machineId,
      device_temperature:machineData.deviceTemperature,
      battery_level: machineData.batteryLevel,
      total_water_addition:machineData.totalWaterAddition,
    })
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
    // this.selectMachine();
    // this.selectUserBalance(this.data.user_id);
    // this.enableDevice("on");
    // this.enableDevice("off");
    // this.enableDeviceWater("on");
    // this.enableDeviceWater("off");
    // this.enableDevicePause("on");
    // this.enableDevicePause("off");
    // this.updateUserBalance(100,this.data.user_id)
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    // 页面隐藏时停止所有定时器
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer()
  },

// 重构写法，绿色完成,红色失败，黄色搁置
// 1.先查询设备属性吧,💚
async selectMachine(e){
  try {
    const machine = await wxp.request({
      url: 'http://localhost:8080/machine/ma1',
      method: "GET",
     })
     console.log(machine.data);
     return machine.data;
  } catch (error) {
    console.error("获取设备属性失败：", error);
    return null; // 返回失败状态
  }
},
// 2.查询用户余额,💛:userid需要确定值
async selectUserBalance(userid){
  try{
    if(userid==""){
      throw new Error("用户id是空的，检查缓存");
    }
    const userBalance = await wxp.request({
    url: `http://localhost:8080/user/${userid}`,
    method:"GET",
   })
   console.log(userBalance.data);
   return userBalance.data;
  } catch (error) {
    console.error("获取用户余额失败：", error);
    return null; // 返回失败状态
  }
},
//3.机器启用的发送指令,💚
async enableDevice(commamd){
  try {
    const result = await wxp.request({
      url: 'http://localhost:8080/machine/enable_device',
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data:{
        water:commamd
      },
    })
    console.log(result)
  } catch (error) {
    console.error("启用设备失败：", error);
    return null;
  }
},
//4.机器开始加水的发送指令,💚
async enableDeviceWater(commamd){
  try {
    const result = await wxp.request({
      url: 'http://localhost:8080/machine/water',
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data:{
        water:commamd
      },
    })
    console.log(result)
  } catch (error) {
    console.error("开水失败：", error);
    return null;
  }
},
//5.机器订单的发送指令（需要确认呢）,💚
async enableDevicePause(commamd){
  try {
    const result = await wxp.request({
      url: 'http://localhost:8080/machine/pause',
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data:{
        water:commamd
      },
    })
    console.log(result)
  } catch (error) {
    console.error("机器订单指令失败：", error);
    return null;
  }
},
//6.修改用户余额,💚
async updateUserBalance(money,userid){
  try {
    if(userid==""){
      throw new Error("用户id是空的，请检查缓存");
    }
    const result = await wxp.request({
      url: 'http://localhost:8080/user/'+userid+'/deduct',
      method:"PATCH",
      header: {
        'content-type': 'application/json'
      },
      data: {
        amount:money
      },
    })
    console.log(result)
  } catch (error) {
    console.error("用户扣钱失败：", error);
    return null;
  }
},
//7.产生交易记录,💛:还没有测
async createTransactionRecords(data){
  try {
    const result = wxp.request({
      url: 'http://localhost:8080/transaction',
      method:"POST",
      header: {
        'content-type': 'application/json'
      },
      data: data,
    })
    console.log(result);
    return result;
  } catch (error) {
    console.error("交易记录创建失败了",error)
    return null;
  }
},
//8.延迟函数,来循环查询的delay
delay(ms){
  new Promise(resolve=>{setTimeout(resolve,ms)})
}

})
