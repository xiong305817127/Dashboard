import { routerRedux } from 'dva/router'
import modelExtend from 'dva-model-extend'
import { login } from 'services/login'
import { model } from 'models/common'

export default modelExtend(model, {
  namespace: 'login',

  state: {
    showPassword:0
  },

  effects: {
    * login ({
      payload,
    }, { put, call, select }) {
      let dev = process.env?process.env.NODE_ENV:"production"
      const data = yield call(login,{ dev:dev , ...payload})
      const { locationQuery } = yield select(_ => _.app)
      if (data.success) {
        const { from } = locationQuery
        yield put({ type: 'app/query' })
        if (from && from !== '/login') {
          yield put(routerRedux.push(from))
        } else {
          yield put(routerRedux.push('/home'))
        }
      } else {
        throw data
      }
    },
  },

})
