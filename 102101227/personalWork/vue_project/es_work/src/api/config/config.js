import axios from 'axios'
import { useRouter } from 'vue-router'
const service = axios.create({
  baseURL: 'http://localhost:9739',
  timeout: 60000,
  /*   withCredentials:true, */
  /* crossDomain:true, */
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})
service.interceptors.request.use(config => {
  config.headers.token = localStorage.getItem('token')
  return config
})
service.interceptors.response.use((res) => { // 成功的请求返回处理
  if (res.data.code == 100) { // 未登录code为100
    useRouter.push({
      path: '/'
    })
  } //else if (res.data.code != 200) {
  //   alert('操作失败')
  // } else if (res.data.msg != '获取成功') {
  //   alert(res.data.msg)
  // }
  return res
},
  (err) => { // 异常的请求返回处理
    alert('请求异常')
    return Promise.reject(err)
  })
export default service
