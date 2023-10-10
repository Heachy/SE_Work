import MyRequest from '../config/config'

const disconnectRoom = (data) => {
    return MyRequest({
        url: '/match/disconnect',
        method: 'post',
        data
    })
}
export default disconnectRoom