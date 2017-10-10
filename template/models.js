/* global window */
import modelExtend from 'dva-model-extend'
import { config } from 'utils'
import * as %%MenuName%%Service from 'services/%%MenuName%%'
import { model } from './common'

const { query } = %%MenuName%%Service
const { prefix } = config

export default modelExtend(model, {
  namespace: '%%MenuName%%',

  state: {
    data:{}
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen((location) => {
        if (location.pathname === '%%MenuRoute%%') {
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
            data:data
          }
        })
      }
    }
  },

  reducers: {

  },
})
