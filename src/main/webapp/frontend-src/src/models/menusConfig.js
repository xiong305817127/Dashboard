/* global window */
import modelExtend from 'dva-model-extend'
import { config } from 'utils'
import * as menuService from 'services/menus'
import * as usersService from 'services/users'
import { model } from './common'

const { query ,update,create,remove } = menuService
const { queryPermissions,deletePermissions,addPermissions,updatePermissions } = usersService
const { prefix } = config

export default modelExtend(model, {
  namespace: 'menusConfig',

  state: {
    user:{},
    menu:[],
    isAdmin:true,
    currentSelectMenu:{},
    showForm:false,
    modalType:"update",
    permissions:[],
    currentPermissions:{},
    checkedPermissions:[],
    showAdd:false
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '/menusConfig') {
          const payload ={}
          dispatch({
            type: 'query',
            payload,
          })
        }
      })
    },
  },

  effects: {

    * query ({ payload = {} }, { call, put }) {
      const { data} = yield call(query, payload)
      if (data) {
        yield  put({
          type: 'updateState',
          payload: {
            menu:data,
            showAdd:false
          }
        })
      }
    },
    * queryPermissions ({ payload = {} }, { call, put }) {
      const { data:permissions } = yield call(queryPermissions, payload)
      if (permissions) {
        yield  put({
          type: 'updateState',
          payload: {
            permissions: permissions.filter((permiss)=>{ return  permiss.role != "admin"}),
            showAdd:false
          }
        })
      }
    },
    * deletePermissions ({ payload = {} }, { call, put }) {
      const { data:permissions } = yield call(deletePermissions, payload)
      if (permissions) {
        yield  put({
          type: 'queryPermissions',
          payload: { }
        })
        yield  put({
          type: 'updateState',
          payload: {
            currentPermissions:{},
            checkedPermissions:[]
          }
        })

      }
    },

    * addPermissions ({ payload = {} }, { call, put }) {
      const { data:permissions } = yield call(addPermissions, payload)
      if (permissions) {
        yield  put({
          type: 'queryPermissions',
          payload: { }
        })
      }
      yield  put({
        type: 'updateState',
        payload: {
          currentPermissions:{},
          checkedPermissions:[]
        }
      })
    },

    * updatePermissions ({ payload = {} }, { call, put }) {
      const { data:permissions } = yield call(updatePermissions, payload)
      if (permissions) {
        yield  put({
          type: 'queryPermissions',
          payload: { }
        })
        yield  put({
          type: 'updateState',
          payload: {
            currentPermissions:{},
            checkedPermissions:[]
          }
        })
      }
    },

    * delete ({ payload }, { call, put, select }) {
      const data = yield call(remove,  payload )
      if (data.success) {
        yield put({ type: 'updateState', payload: { currentSelectMenu: {} } })
        yield put({ type: 'app/query' })
        yield put({ type: 'query' })
      } else {
        throw data
      }
    },

    * create ({ payload }, { call, put }) {
      const data = yield call(create, payload)
      if (data.success) {
        yield put({ type: 'app/query' })
        yield put({ type: 'query' })

      } else {
        throw data
      }
    },

    * update ({ payload }, { select, call, put }) {
      const id = yield select(({ menusConfig }) => menusConfig.currentSelectMenu.id)
      const data = yield call(update, payload)
      if (data.success) {
        yield put({ type: 'app/query' })
        yield put({ type: 'query' })
      } else {
        throw data
      }
    },

  },

  reducers: {

  },
})
