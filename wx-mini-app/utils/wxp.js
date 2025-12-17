 // 封装接口api
  /**
 * 将 wx.request 封装成 Promise 版本
 * 
@param {object} options
 - 原始 wx.request 的所有参数
@returns {Promise<any>} 
- 兑现时返回服务器数据
 */
function promisifyWxRequest(options){
  return new Promise((resolve,reject)=>{
    wx.request({
      ...options,
      success:(res)=>{
        if(res.statusCode>=200&&res.statusCode<=300){
          resolve(res.data);
        }else{
          const error = new Error(`请求失败，状态码: ${res.statusCode}`);
          error.response = res;
          reject(error);
        }
      },
      fail:(err)=>{
        reject(new Error(`网络或客户端错误: ${err.errMsg}`));
      }
    })
  })
}
function getStorage(options){
  return new Promise((resolve, reject) => {
      wx.getStorage({
          ...options,
          success: resolve, // 成功直接 resolve
          fail: reject     // 失败直接 reject
      });
  });
}
// 组织并导出函数
const wxp = {
  request: promisifyWxRequest,
  getStorage:getStorage
  // 如果以后有其他工具函数，如 api.upload, api.login 等，也放在这里
};

// ⭐ 核心步骤：使用 CommonJS 规范导出
module.exports = wxp;