// pages/recharge/recharge.js
Page({
  data: {
    displayAmount: "0.00",
    canRecharge: false,
    userBalance: 0,
    selectedAmount: 0,
    customAmount: '',
    isCustom: false,
    paymentMethod: 'wechat',
    amountList: [
      { value: 10.0, bonus: 1 },
      { value: 30.0, bonus: 3 },
      { value: 50.0, bonus: 5 },
      { value: 100.0, bonus: 12 },
      { value: 200.0, bonus: 25 },
      { value: 500.0, bonus: 70 }
    ]
  },

  //返回上一页
  backTab(e){
    wx.navigateBack({
      delta: 1
    });
  },

  // 选择固定金额
  selectAmount: function(e) {
    const amount = parseInt(e.currentTarget.dataset.amount)
    this.setData({
      selectedAmount:parseFloat(amount.toFixed(1)),
      customAmount: '',
      isCustom: false
    }, () => {
      this.updateDisplayAmount()
    })
  },

  // 自定义金额输入
  onCustomAmountInput: function(e) {
    const value = e.detail.value
    this.setData({
      customAmount: value,
      selectedAmount: 0,
      isCustom: true
    }, () => {
      this.updateDisplayAmount()
    })
  },

  // 自定义金额获得焦点时清空固定金额选择
  onCustomAmountFocus: function() {
    this.setData({
      selectedAmount: 0,
      isCustom: true
    })
  },

  // 选择付款方式
  selectPaymentMethod: function(e) {
    const method = e.currentTarget.dataset.method
    this.setData({
      paymentMethod: method
    })
  },

  // 更新显示金额和按钮状态
  updateDisplayAmount: function() {
    const amount = this.computedDisplayAmount()
    const canRecharge = amount > 0 && amount <= 10000
    
    this.setData({
      displayAmount: amount.toFixed(2),
      canRecharge: canRecharge
    })
  },

  // 计算显示金额
  computedDisplayAmount: function() {
    if (this.data.isCustom && this.data.customAmount) {
      return parseFloat(this.data.customAmount) || 0
    } else {
      return this.data.selectedAmount
    }
  },

  // 立即充值
  onRecharge: function() {
    if (!this.data.canRecharge) {
      wx.showToast({
        title: '请选择充值金额',
        icon: 'none'
      })
      return
    }

    const amount = this.computedDisplayAmount()
    const paymentMethod = this.data.paymentMethod
    
    this.showConfirmDialog(amount, paymentMethod)
  },

  // 显示确认弹窗
  showConfirmDialog: function(amount, paymentMethod) {
    const methodName = paymentMethod === 'wechat' ? '微信支付' : '支付宝'
    
    wx.showModal({
      title: '确认充值',
      content: `充值金额：¥${amount.toFixed(2)}\n付款方式：${methodName}`,
      confirmText: '立即支付',
      cancelText: '再想想',
      success: (res) => {
        if (res.confirm) {
          this.processPayment(amount, paymentMethod)
        }
      }
    })
  },

 // 处理支付 - 调试版本
processPayment: function(amount, paymentMethod) {
  wx.showLoading({
    title: '发起支付中...',
  })

  const rechargeAmount = Number(amount.toFixed(2));
  
  // 构建请求数据
  const requestData = {
    amount: rechargeAmount,
    remark: paymentMethod === 'wechat' ? '微信支付' : '支付宝支付',
  };
  // 调用后端支付接口
  wx.request({
    url: 'http://localhost:8080/user/' + this.data.userId + '/recharge',
    method: "PATCH",
    header: { 
      'content-type': 'application/json' 
    },
    data: JSON.stringify(requestData),
    success: (res) => {
      wx.hideLoading()
      if (res.data.code === 200) {
        wx.showToast({
          title: '充值成功',
          icon: 'success',
          duration: 2000
        })
        this.refreshUserBalance()
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        wx.showToast({
          title: res.data.message || '支付失败',
          icon: 'error',
          duration: 3000
        })
      }
    },
    fail: (err) => {
      wx.hideLoading()
      console.error('完整错误信息:', err);
      wx.showToast({
        title: '网络错误，请重试',
        icon: 'error'
      })
    }
  })
},

  // 微信支付
  wechatPay: function(paymentParams) {
    // 注意：这里需要根据后端返回的实际支付参数进行调整
    if (paymentParams.prepayId) {
      // 如果后端返回了微信支付所需的参数
      wx.requestPayment({
        timeStamp: paymentParams.timeStamp,
        nonceStr: paymentParams.nonceStr,
        package: paymentParams.package,
        signType: paymentParams.signType,
        paySign: paymentParams.paySign,
        success: (res) => {
          wx.showToast({
            title: '支付成功',
            icon: 'success',
            duration: 2000
          })
          setTimeout(() => {
            this.refreshUserBalance()
            wx.navigateBack()
          }, 1500)
        },
        fail: (err) => {
          console.error('支付失败:', err)
          if (err.errMsg === 'requestPayment:fail cancel') {
            wx.showToast({
              title: '支付已取消',
              icon: 'none'
            })
          } else {
            wx.showToast({
              title: '支付失败，请重试',
              icon: 'error'
            })
          }
        }
      })
    } else {
      // 如果后端没有返回支付参数，说明是模拟支付
      wx.showToast({
        title: '充值成功',
        icon: 'success',
        duration: 2000
      })
      setTimeout(() => {
        this.refreshUserBalance()
        wx.navigateBack()
      }, 1500)
    }
  },

  // 支付宝支付处理
  handleAlipay: function(paymentParams) {
    // 在小程序环境中通常无法直接调用支付宝支付
    wx.showModal({
      title: '支付宝支付',
      content: '小程序内暂不支持支付宝支付，请使用微信支付',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 刷新用户余额
  refreshUserBalance: function() {
    wx.request({
      url: 'http://localhost:8080/user/'+this.data.userId,
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

  onLoad: function(options) {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userId: userInfo.userId,
        userBalance: userInfo.balance || 0
      })
    }
  },

  onShow: function() {
    this.refreshUserBalance()
  }
})