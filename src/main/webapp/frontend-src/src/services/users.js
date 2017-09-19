import { request, config } from 'utils'

const { api } = config
const { users , permissions ,permission} = api

export async function query (params) {
  return request({
    url: users,
    method: 'get',
    data: params,
  })
}

export async function remove (params) {
  return request({
    url: users,
    method: 'delete',
    data: params,
  })
}

export async function queryPermissions (params) {
  return request({
    url: permissions,
    method: 'get',
    data: params,
  })
}

export async function deletePermissions (params) {
  return request({
    url: permission,
    method: 'delete',
    data: params,
  })
}


export async function addPermissions (params) {
  return request({
    url: permissions,
    method: 'post',
    data: params,
  })
}

export async function updatePermissions (params) {
  return request({
    url: permission,
    method: 'patch',
    data: params,
  })
}
