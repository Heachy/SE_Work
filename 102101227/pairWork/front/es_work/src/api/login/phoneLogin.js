import MyRequest from '../config/config'

const phoneLogin = (data) => {
    return MyRequest({
        url: '/user/login',
        method: 'post',
        data
    })
}
export default phoneLogin