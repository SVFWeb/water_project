
const wxp = require('../../utils/wxp');
Page({
  data: {
    // 状态管理
    is_polling:false,
    is_pause: 0,
    is_watering: 0,
    showLoginModal: false,
    showBalanceModal: false,
    isLoading: false,
    is_connect: 0,//连接设备
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
    pause:"0",
    waterAddSwitch:"2",
    enableDevice:"1",

    //计时器
    seconds: 0, // 当前秒数
    timer: null, // 计时器 ID
    formattedTime: '00:00'
  },
  //计时器
  formatTime: function(totalSeconds) {
    // ... (上面定义的 formatTime 函数体)
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
  },
  //开始计时
  startTimer: function() {
    if (this.data.timer !== null) return;

    let intervalId = setInterval(() => {
      // 1. 更新总秒数
      const newSeconds = this.data.seconds + 1;

      // 2. 格式化秒数
      const newFormattedTime = this.formatTime(newSeconds);

      // 3. 一次性更新两个数据
      this.setData({
        seconds: newSeconds,
        formattedTime: newFormattedTime // 更新格式化后的时间
      });
    }, 1000);

    this.setData({
      timer: intervalId
    });
  },
  // 停止计时 (暂停)
  stopTimer: function() {
    if (this.data.timer !== null) {
      clearInterval(this.data.timer);
      console.log("计时器已停止，ID:", this.data.timer);
      this.setData({
        timer: null
      });
    }
  },
  //清零计时
  resetTimer: function() {
    this.stopTimer(); 
    this.setData({
      seconds: 0,
      formattedTime: '00:00' // 清零时也重置格式化时间
    });
  },
  // 返回上一页
  backTab() {
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
  //计算价钱
  calculateAmount(){
    const Amount = Number((this.data.total_water_addition/1000)*0.27)|| 0
    console.log()
    return Amount;
  },
  //订单发送信息
  orderInformation(Amount){
    const transactionData = {
      userId: this.data.user_id,
      machineId: this.data.machine_id,
      waterUnitPrice:"0.27",
      totalLiters: this.data.total_water_addition,
      finalAmount: Amount,
    };
    return transactionData;
  },
  // 连接设备
 async connectedDevice(e){
    // 判断设备是否连接
    if(this.data.is_connect){
      wx.showToast({
        title: '已连接设备',
      })
      return;
    }

    try {
      // 1. 获取登录状态 (使用 await 等待 Promise 解包)
      const loginRes = await wxp.getStorage({ key: "isLogin" });
      // 2. 判断是否在登录状态
      if (loginRes.data) { 
          // 3. 查余额是否有钱 (使用 await 等待 Promise 解包)
          const haveBalance = await this.selectUserBalance(this.data.user_id);
          if (haveBalance.balance > 0) {
              // 钱足够，执行开水等操作
              this.enableDevice("on");
              this.setData({ is_connect: 1 });
          } else {
              // 余额不足
              this.setData({ showBalanceModal: true });
              throw new Error ("用户余额不足")
          }
          // 开计时
          this.startTimer();
          // 开始轮询
          this.startMachinePolling(true);
      } else {
          // 没登录
          this.setData({ showLoginModal: true });
          throw new Error ("用户未登录");
      }
    } catch (error) {
        // 捕获所有错误 (如 wx.getStorage 失败，或 selectUserBalance 抛出的错误)
        console.error("设备连接流程失败:", error);
        wx.showToast({ title: '操作失败', icon: 'none' });
    }
  },
  //开始加水
  startAddingWater(e){
    if(this.data.is_connect){
      if(this.data.is_watering==0&&this.data.is_pause==1){
      this.enableDeviceWater("on");
      this.enableDevicePause("on");
      this.setData({
        // 没暂停
        is_pause:0,
        // 正在加水
        is_watering:1
      })
      wx.showToast({
        title: '继续加水',
        icon:'success'
      })
      }else if(this.data.is_watering==0&&this.data.is_pause==0){
        this.enableDeviceWater("on");
        this.enableDevicePause("on");
        this.setData({
          // 没暂停
          is_pause:0,
          // 正在加水
          is_watering:1
        })
        wx.showToast({
          title: '开始加水',
          icon:'success'
        })
      }else{
        wx.showToast({
          title: '已经在加水中',
          icon:'none'
        })
      }
      
    }else{
      wx.showToast({
        title: '您还未连接设备',
        icon:"none"
      })
    }
  },
  // 暂停加水
  pauseDevice(e){
    if(this.data.is_connect){
      if(this.data.is_pause){
        wx.showToast({
          title: '已暂停设备',
          icon:"none"
        })
      }else{
        this.enableDevicePause("off");
        this.setData({
          // 暂停了
          is_pause:1,
          //可以开水
          is_watering:0
        })
        wx.showToast({
          title: '暂停加水',
          icon:"error"
        })
      }
    }else{
      wx.showToast({
        title: '您还未连接设备',
        icon:"none"
      })
    }
  },
  // 结束加水
  endAddingWater(e){
    if(this.data.is_connect){
      this.enableDeviceWater("off");
      this.enableDevicePause("off");
      this.enableDevice("off");
      this.resetTimer();
      this.startMachinePolling(false);
      this.setData({
        is_connect:0,
        is_polling:false
      })
      // 算钱
      let Amount = this.calculateAmount();
      console.log(Amount)
      if(Amount==0){
        wx.showToast({
          title: '感谢您的使用',
          icon:'success'
        })
      }else{
        let transactionData = this.orderInformation(Amount);
        this.createTransactionRecords(transactionData);
        this.updateUserBalance(Amount,this.data.user_id)
        wx.showModal({
          title: '提示',
          content: "共消耗"+this.data.total_water_addition+"毫升，消费"+Amount+"元",
          complete: (res) => {
            if (res.cancel) {
            }
            if (res.confirm) {
            }
          }
        })
      }
    }else{
      wx.showToast({
        title: '您还未连接设备',
        icon:"none"
      })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  async onLoad(options) {
    // 可以在这里初始化一些数据

    //查机器数据
    const machineData= await this.selectMachine();
    this.setData({
      machine_status: machineData.status,
      machine_id: machineData.machineId,
      device_temperature:machineData.deviceTemperature,
      battery_level: machineData.batteryLevel,
      total_water_addition:machineData.totalWaterAddition,
      pause:machineData.pause,
      waterAddSwitch:machineData.waterAddSwitch,
      enableDevice:machineData.enableDevice
    })
    // 查用户信息,和展示余额
    wx.getStorage({
      key:"userInfo",
      success:(res)=>{
        this.setData({
          user_id:res.data.userId,
          userBalance:res.data.balance,
          water_yield:(res.data.balance/0.27).toFixed(2)
        })
      },
      fail:(err)=>{
        throw new Error("获取用户信息失败：",err)
      }
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
    this.endAddingWater()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },
  observers: {
    // 监听字段顺序: pause, waterAddSwitch, enableDevice
    "pause,waterAddSwitch,enableDevice": function(newPause, newWaterAddSwitch, newEnableDevice) {
      // 1. 获取新值并使用
      // 这里的 newPause, newWaterAddSwitch, newEnableDevice 就是 data 字段的最新值
      
      // 开水命令
      if (newWaterAddSwitch === "1" && newPause === "1") {
        console.log("触发：开始加水");
        this.startAddingWater();
      }
      
      // 暂停
      if (newPause == "0") {
        console.log("触发：暂停");
        this.pauseDevice();
      }
      
      if (newWaterAddSwitch === "0" && newEnableDevice === "0"&&newPause === "0") {
        console.log("触发：结束加水");
        this.endAddingWater();
      }
      
    }
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
  return new Promise(resolve=>{setTimeout(resolve,ms)})
},
// 9.轮询查询设备属性，直到获取到有效数据
async startMachinePolling() {
  if (this.data.is_polling) {
      console.warn('轮询已在运行，不再重复启动。');
      return;
  }
  // 启动轮询前，设置状态为 true
  this.setData({ 
    is_polling: true,
  });
  
  const POLLING_INTERVAL = 1000;
  
  // 检查全局状态
  while (this.data.is_polling) { 
      try {
          // 1. 调用查询接口
          const machineData = await this.selectMachine();
          
          // 在 await 之后再次检查状态，避免在等待期间状态被修改后继续执行
          if (!this.data.is_polling) break; 
          
          this.setData({
              machine_status: machineData.status,
              machine_id: machineData.machineId,
              device_temperature:machineData.deviceTemperature,
              battery_level: machineData.batteryLevel,
              total_water_addition:machineData.totalWaterAddition,
              pause:machineData.pause,
              waterAddSwitch:machineData.waterAddSwitch,
              enableDevice:machineData.enableDevice
          });
          await this.delay(POLLING_INTERVAL);
          
      } catch (error) {
          console.error(`查询失败，尝试重试... (${error.message})`);
          await this.delay(POLLING_INTERVAL);
      }
  }
  
  // 循环结束后，确保状态设为 false
  this.setData({ is_polling: false });
  console.log('轮询已停止。');
}
})
