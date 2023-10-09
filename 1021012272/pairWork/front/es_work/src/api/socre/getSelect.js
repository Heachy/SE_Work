import MyRequest from '../config/config'

const getSelect = (data) => {
    return MyRequest({
        url: '/score/select',
        method: 'post',
        data
    })

}
export default getSelect