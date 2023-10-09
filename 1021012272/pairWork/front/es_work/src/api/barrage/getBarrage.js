import MyRequest from '../config/config'

const getBarrageApi = () => {
    return MyRequest({
        url: '/barrage/get',
        method: 'get',
    })
}
export default getBarrageApi