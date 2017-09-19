import { request, config } from 'utils'

const { api } = config
const { home } = api

export async function query (params) {
  return request({
    url: home,
    method: 'get',
    data: params,
  })
}
