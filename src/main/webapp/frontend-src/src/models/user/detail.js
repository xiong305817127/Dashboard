import pathToRegexp from 'path-to-regexp'
import { query } from '../../services/user'

export default {

  namespace: 'userDetail',

  state: {
    data: {},
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen(({ pathname }) => {
        const match = pathToRegexp('/user/:id').exec(pathname)
        if (match) {
          dispatch({ type: 'query', payload: { id: match[1] } })
        }
      })
    },
  },

  effects: {
    * query ({
      payload,
    }, { call, put }) {
      const result = yield call(query, payload)
      const { success, message, status, data } = result
      if (success) {
        const {permissions,id ,password,...other } = data
        yield put({
          type: 'querySuccess',
          payload: {
            data: other,
          },
        })
      } else {
        throw result
      }
    },
  },

  reducers: {
    querySuccess (state, { payload }) {
      const { data } = payload
      return {
        ...state,
        data,
      }
    },
  },
}
