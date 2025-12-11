Component({
  data: {
    selected: 0,
    color: "#7A7E83",
    selectedColor: "#1296db",
    list: [{
      "pagePath": "/pages/index/index",
      "iconPath": "/image/home.png",
      "selectedIconPath": "/image/home-hl.png",
      "text": "首页"
    },
    {
      "pagePath": "/pages/watering/watering",
      "iconPath": "/image/water.png",
      "selectedIconPath": "/image/water-hl.png",
      "text": "机器"
    },
    {
      "pagePath": "/pages/logs/logs",
      "iconPath": "/image/saoma.png",
      "selectedIconPath": "/image/saoma-hl.png",
      "text": "扫码"
    },
    {
      "pagePath": "/pages/order/order",
      "iconPath": "/image/order.png",
      "selectedIconPath": "/image/order-hl.png",
      "text": "订单"
    },
    {
      "pagePath": "/pages/user/user",
      "iconPath": "/image/user.png",
      "selectedIconPath": "/image/user-hl.png",
      "text": "我的"
    }]
  },
  attached() {
    this.updateSelected();
  },
  methods: {
    updateSelected(activePath) {
      let currentPath = activePath;
      if (!currentPath) {
        const page = getCurrentPages().pop();
        currentPath = page ? page.route : '';
      }
      // 确保 currentPath 以斜杠开头
      if (currentPath && currentPath[0] !== '/') {
        currentPath = '/' + currentPath;
      }
      const list = this.data.list;
      for (let i = 0; i < list.length; i++) {
        if (list[i].pagePath === currentPath) {
          this.setData({
            selected: i
          });
          break;
        }
      }
    },
    switchTab(e) {
      const data = e.currentTarget.dataset;
      const url = data.path;
      const index = data.index;
      if(index ==2 ){
        wx.scanCode({
          success: (res) => {
            console.log(res)
            // 这里可以处理扫码结果，例如跳转到相应页面等
            const machineID = res.result;

            wx.navigateTo({
              url: `/pages/machine/machine?id=${machineID}`
            })

          },
          fail: (err) => {
            console.error(err)
          }
          
        })
        return;
      }
      wx.switchTab({url});
      this.setData({
        selected: data.index
      });
    }
  }
});