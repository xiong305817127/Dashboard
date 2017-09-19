import { request, config } from 'utils'

const { api } = config
const { menus,menu } = api

export async function query (params) {
  return request({
    url: menus,
    method: 'get',
    data: params,
  })
}


export async function create (params) {
  return request({
    url: menus,
    method: 'post',
    data: params,
  })
}

export async function update (params) {
  return request({
    url: menu,
    method: 'patch',
    data: params,
  })
}


export async function remove (params) {
  return request({
    url: menu,
    method: 'delete',
    data: params,
  })
}
