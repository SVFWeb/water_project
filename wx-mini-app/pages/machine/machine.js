// pages/machine/machine.js
function debounce(func, wait = 1000, immediate = false) {
  let timeout, result
  return function (...args) {
    const context = this
    if (timeout) clearTimeout(timeout)
    if (immediate) {
      const callNow = !timeout
      timeout = setTimeout(() => {
        timeout = null
      }, wait)
      if (callNow) result = func.apply(context, args)
    } else {
      timeout = setTimeout(() => {
        func.apply(context, args)
      }, wait)
    }
    return result
  }
}

Page({
  data: {
    is_pause: 0,
    is_watering: 0,
    showLoginModal: false,
    showBalanceModal: false,
    isLoading: false,
    machine_status: "离线",
    user_id: "",
    machineNum: "001",
    rate_sum: 0.27,
    price_per_liter: 0.004,
    service_fee: 0.266,
    userBalance: 0,
    water_yield: 0,
    machine_id: "0",
    device_temperature: 0,
    battery_level: 0,
    is_connect: 0,
    total_water_addition: 0,
    
    // 计时器相关数据
    connectionTime: "00:00",
    connectionTimer: null,
    connectionStartTime: 0,
    
    // 新增：设备信息查询定时器
    deviceInfoTimer: null,
    queryInterval: 3000, // 3秒查询一次
  },

  // 返回上一页
  backTab() {
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer() // 停止设备信息查询
    wx.navigateBack({
      delta: 1
    });
  },

  /**
   * 开始连接计时器
   */
  startConnectionTimer() {
    this.stopConnectionTimer()
    
    const startTime = Date.now()
    this.setData({
      connectionStartTime: startTime
    })
    
    const timer = setInterval(() => {
      const currentTime = Date.now()
      const elapsedSeconds = Math.floor((currentTime - this.data.connectionStartTime) / 1000)
      
      const minutes = Math.floor(elapsedSeconds / 60)
      const seconds = elapsedSeconds % 60
      const timeString = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      
      this.setData({
        connectionTime: timeString
      })
    }, 1000)
    
    this.setData({
      connectionTimer: timer
    })
  },

  /**
   * 停止连接计时器
   */
  stopConnectionTimer() {
    if (this.data.connectionTimer) {
      clearInterval(this.data.connectionTimer)
      this.setData({
        connectionTimer: null,
        connectionTime: "00:00",
        connectionStartTime: 0
      })
    }
  },

  /**
   * 开始设备信息查询定时器
   */
  startDeviceInfoTimer() {
    this.stopDeviceInfoTimer() // 先停止之前的
    
    console.log('开始定时查询设备信息...')
    
    // 立即查询一次
    this.getMachineInfo()
    
    // 启动定时器
    const timer = setInterval(() => {
      this.getMachineInfo()
    }, this.data.queryInterval)
    
    this.setData({
      deviceInfoTimer: timer
    })
  },

  /**
   * 停止设备信息查询定时器
   */
  stopDeviceInfoTimer() {
    if (this.data.deviceInfoTimer) {
      clearInterval(this.data.deviceInfoTimer)
      console.log('停止定时查询设备信息')
      this.setData({
        deviceInfoTimer: null
      })
    }
  },

  /**
   * 获取设备信息（封装成独立方法）
   */
  getMachineInfo() {
    // 只有在连接状态才查询
    if (!this.data.is_connect) {
      return
    }
    
    wx.request({
      url: 'http://localhost:8080/machine/ma1',
      method: "GET",
      success: (res) => {
        if (res.data && res.data.data) {
          console.log('定时查询设备信息:', res.data.data)
          const { 
            status, 
            machineId, 
            deviceTemperature, 
            batteryLevel, 
            totalWaterAddition,
            waterTank,        // 水箱状态
            fillUp,           // 是否加满
            waterAddSwitch,   // 开水开关状态
            pause             // 暂停状态
          } = res.data.data;
          
          this.setData({
            machine_status: status || this.data.machine_status,
            machine_id: machineId || this.data.machine_id,
            device_temperature: deviceTemperature || this.data.device_temperature,
            battery_level: batteryLevel || this.data.battery_level,
            total_water_addition: totalWaterAddition || this.data.total_water_addition,
            // 可以添加更多实时状态
          })
          
          // 同步设备状态到界面状态（可选）
          this.syncDeviceState(res.data.data)
        }
      },
      fail: (err) => {
        console.log('设备信息查询失败:', err)
      }
    })
  },

  /**
   * 同步设备状态到界面状态
   */
  syncDeviceState(deviceData) {
    // 根据设备返回的状态同步界面状态
    if (deviceData.waterAddSwitch === 'OFF' && this.data.is_watering === 1) {
      // 设备显示关闭但界面显示正在加水，需要同步
      console.log('检测到设备状态不同步，正在同步...')
      this.setData({
        is_watering: 0
      })
    }
    
    if (deviceData.pause === 'ON' && this.data.is_pause === 0) {
      // 设备显示暂停但界面显示未暂停
      this.setData({
        is_pause: 1
      })
    }
    
    // 可以根据需要添加更多状态同步逻辑
  },

  // 格式化时间
  formatTime(seconds) {
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
  },

  // 检查登录状态
  checkLoginStatus() {
    return new Promise((resolve, reject) => {
      wx.getStorage({
        key: "isLogin",
        success: (res) => {
          if (res.data) {
            resolve(true)
          } else {
            reject(new Error('NOT_LOGGED_IN'))
          }
        },
        fail: (err) => {
          reject(new Error('NOT_LOGGED_IN'))
        }
      })
    })
  },

  // 检查余额
  checkBalance() {
    return new Promise((resolve, reject) => {
      if (this.data.userBalance > 0) {
        resolve(true)
      } else {
        reject(new Error('INSUFFICIENT_BALANCE'))
      }
    })
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

  // 连接设备
  connectedDevice: debounce(function(e) {
    if (this.data.isLoading) return
    
    this.checkLoginStatus().then((isLoggedIn) => {
      return this.checkBalance()
    }).then((balanceEnough) => {
      this.setData({ isLoading: true })
      this.connectToDevice()
    }).catch((error) => {
      console.log("出错了:", error.message)
      
      if (error.message === 'NOT_LOGGED_IN') {
        this.showLoginModal()
      } else if (error.message === 'INSUFFICIENT_BALANCE') {
        this.showBalanceModal()
      } else {
        wx.showToast({
          title: '系统错误，请重试',
          icon: 'error'
        })
      }
      this.setData({ isLoading: false })
    })
  }, 1000, true),

  connectToDevice: function() {
    if(this.data.is_connect==0){
      // 订单开始
      wx.request({
        url: 'http://localhost:8080/machine/enable_device',
        method: 'POST',
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        data:{
          water:"on"
        },
        success: (res) => {
          if (res.data.code==200) {
                wx.showToast({
                  title: '设备连接成功',
                  icon: 'success'
                })
                this.setData({ 
                  isLoading: false,
                  is_connect: 1
                })
                // 连接成功后开始计时和定时查询
                this.startConnectionTimer()
                this.startDeviceInfoTimer() // 新增：开始定时查询设备信息
              

          } else {
            wx.showToast({
              title: res.data.message || '连接失败',
              icon: 'error'
            })
            this.setData({ isLoading: false })
          }
        },
        
        fail: (err) => {
          wx.showToast({
            title: '网络错误，请重试',
            icon: 'error'
          })
          this.setData({ isLoading: false })
        }
      })
    } else {
      wx.showToast({
        title: '已连接设备',
      })
    }
  },

  //开始加水
  startAddingWater: debounce(function(e) {
    if(this.data.is_connect){
      if(this.data.is_watering==0){
        wx.request({
          url: 'http://localhost:8080/machine/water',
          method:"POST",
          data:{
            water:"on",
          },
          header: {
            'content-type': 'application/x-www-form-urlencoded'
          },
          success:(res)=>{
            console.log(res.data)
            // 第二个连接
            wx.request({
              url: 'http://localhost:8080/machine/pause',
              method:"POST",
              data:{
                water:"on",
              },
              header: {
                'content-type': 'application/x-www-form-urlencoded'
              },
              success:(res)=>{
                wx.showToast({
                  title: "开始加水",
                  icon:'success'
                })
                this.setData({
                  is_watering:1
                })
              }
            })
           
          },
          fail:(err)=>{
            console.log(err.data)
          }
        })
      } else {
        wx.showToast({
          title: '您已经在加水了',
        })
      }
    } else {
      wx.showToast({
        title: '您还未连接设备',
        icon:'error'
      })
    }
  }, 800),

  //暂停设备
  pauseDevice: debounce(function(e) {
    if(this.data.is_connect){
      if(this.data.is_pause==0){
        this.setData({
          is_pause:1
        })
        wx.request({
          url: 'http://localhost:8080/machine/water',
          method:"POST",
          data:{
            water:"off",
          },
          header: {
            'content-type': 'application/x-www-form-urlencoded'
          },
          success:(res)=>{
            console.log(res.data)
            wx.showToast({
              title: "暂停加水",
              icon:'success'
            })
          },
          fail:(err)=>{
            console.log(err.data)
          }
        })
      } else {
        wx.showToast({
          title: '取消暂停继续加水',
        })
        wx.request({
          url: 'http://localhost:8080/machine/water',
          method:"POST",
          data:{
            water:"on",
          },
          header: {
            'content-type': 'application/x-www-form-urlencoded'
          },
          success:(res)=>{
            console.log(res.data)
            wx.showToast({
              title: "继续加水",
              icon:'success'
            })
          },
          fail:(err)=>{
            console.log(err.data)
          }
        })
      }
    } else {
      wx.showToast({
        title: '您还未连接设备',
        icon:'error'
      })
    }
  }, 500),

  //结束加水
  endAddingWater: debounce(function(e) {
    if(this.data.is_connect){
      this.setData({
        is_connect:0,
        isLoading:false,
        is_watering:0,  // 重置加水状态
        is_pause:0      // 重置暂停状态
      })
      
      // 停止所有定时器
      this.stopConnectionTimer()
      this.stopDeviceInfoTimer() // 新增：停止设备信息查询
      
      // 发送结束加水请求
      wx.request({
        url: 'http://localhost:8080/machine/water',
        method:"POST",
        data:{
          water:"off",
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        success:(res)=>{
          console.log(res.data)
          wx.showToast({
            title: "结束加水",
            icon:'success'
          })
        },
        fail:(err)=>{
          console.log(err.data)
        }
      })
      
      // 断开设备连接
      wx.request({
        url: 'http://localhost:8080/machine/pause',
        method: 'POST',
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        data:{
          water:"off"
        },
        success: (res) => {
          if (res.data.code==200) {
            wx.showToast({
              title: '设备断开连接',
              icon: 'success'
            })
          } else {
            wx.showToast({
              title: res.data.message || '结束失败',
              icon: 'error'
            })
          }
        },
        fail: (err) => {
          wx.showToast({
            title: '网络错误，请重试',
            icon: 'error'
          })
        }
      })
      // 断开设备连接en
      wx.request({
        url: 'http://localhost:8080/machine/enable_device',
        method: 'POST',
        header: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        data:{
          water:"off"
        },
        success: (res) => {
          if (res.data.code==200) {
            wx.showToast({
              title: '设备断开连接',
              icon: 'success'
            })
          } else {
            wx.showToast({
              title: res.data.message || '结束失败',
              icon: 'error'
            })
          }
        },
        fail: (err) => {
          wx.showToast({
            title: '网络错误，请重试',
            icon: 'error'
          })
        }
      })
      wx.showLoading({
        title: '创建交易中...',
      });
      let finalAmount = ((this.data.total_water_addition*0.27)/1000).toFixed(2)
      const transactionData = {
        userId: this.data.user_id,
        machineId: this.data.machine_id,
        waterUnitPrice:"0.27",
        totalLiters: this.data.total_water_addition,
        finalAmount: finalAmount,
      };
      // 开始扣款
      wx.request({
        url: 'http://localhost:8080/user/'+this.data.user_id+'/deduct',
        method:"PATCH",
        header: {
          'content-type': 'application/json'
        },
        data: {
          amount:finalAmount
        },
        success: (res) => {
          console.log(res.data)
          wx.showToast({
            title: res.data.data.message,
          })
        },
        fail:(res)=>{
          wx.showToast({
            title: res.data,
          })
        }
      })
      // 产生交易记录
      wx.request({
        url: 'http://localhost:8080/transaction',
        method:"POST",
        header: {
          'content-type': 'application/json'
        },
        data: transactionData,
        success: (res) => {
          wx.hideLoading();
          console.log('创建交易响应:', res.data);
          if (res.data.code === 200) {

            wx.showModal({
              title:'提示',
              content:"消耗:"+res.data.data.data.totalLiters+"升，消费:"+res.data.data.data.finalAmount+"元",
              success:(res)=>{
                if (res.confirm) {
                  console.log('用户点击确定')
                  this.refreshUserBalance();
                } else if (res.cancel) {
                  console.log('用户点击取消')
                }
              }
            })
          }
      }
      })
    } else {
      wx.showToast({
        title: '您还未连接设备',
        icon:'error'
      })
    }
  }, 800),

  /**
   * 更新用户余额
   */
  updateUserBalance() {
    wx.getStorage({
      key: 'userInfo',
      success: (res) => {
        const { userId, balance } = res.data
        this.setData({
          user_id: userId,
          userBalance: balance,
          water_yield: (balance / this.data.rate_sum).toFixed(2)
        })
      }
    })
  },
   // 刷新用户余额
   refreshUserBalance: function() {
    wx.request({
      url: 'http://localhost:8080/user/'+this.data.user_id,
      method:"GET",
      success:(res)=>{
        console.log(res.data.data)
        wx.setStorage({
          key:"userInfo",
          data:res.data.data
        })
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 可以在这里初始化一些数据
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    // 获取本地缓存
    wx.getStorage({
      key: 'userInfo',
      success: (res) => {
        const { userId, balance } = res.data
        this.setData({
          user_id: userId,
          userBalance: balance,
          water_yield: (balance / this.data.rate_sum).toFixed(2)
        })
      }
    })
    
    // 获取机器信息（初始加载）
    this.getMachineInfo()
    
    // 停止之前的计时器
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer()
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer() // 页面显示时停止定时查询
    
    // 如果处于连接状态，重新开始定时查询
    if (this.data.is_connect) {
      this.startDeviceInfoTimer()
    }
    
    // 每次页面显示时检查登录状态，更新用户信息
    this.checkLoginStatus().then((isLoggedIn) => {
      wx.getStorage({
        key: 'userInfo',
        success: (res) => {
          const { userId, balance } = res.data
          this.setData({
            user_id: userId,
            userBalance: balance,
            water_yield: (balance / this.data.rate_sum).toFixed(2)
          })
        }
      })
    }).catch((notLoggedIn) => {
      this.setData({
        user_id: "",
        userBalance: 0,
        water_yield: 0
      })
    })
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 页面卸载时停止所有定时器
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    // 页面隐藏时停止所有定时器
    this.stopConnectionTimer()
    this.stopDeviceInfoTimer()
  }
})