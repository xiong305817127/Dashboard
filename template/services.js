import { request, config } from 'utils'

const { api } = config
const { %%MenuName%% } = api

export async function query (params) {
  return request({
    url: %%MenuName%%,
    method: 'get',
    data: params,
  })
}


