import MyRequest from '../config/config'

const connectRoom = () => {
    return MyRequest({
        url: '/match/connect',
        method: 'get',
    })
}
export default connectRoom