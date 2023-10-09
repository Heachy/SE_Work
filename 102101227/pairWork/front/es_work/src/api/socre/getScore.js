import MyRequest from '../config/config'

const getScore = (data) => {
    return MyRequest({
        url: '/score/get',
        method: 'post',
        data
    })

}
export default getScore