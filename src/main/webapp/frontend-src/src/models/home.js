import { parse } from 'qs'
import modelExtend from 'dva-model-extend'
import { query } from 'services/home'
import { model } from 'models/common'
import * as weatherService from 'services/weather'

export default modelExtend(model, {
  namespace: 'home',
  state: {
    weather: {
      city: '深圳',
      temperature: '30',
      name: '晴',
      icon: '//s5.sencdn.com/web/icons/3d_50/2.png',
    },
    sales: [],
    quote: {
      avatar: 'http://img.hb.aicdn.com/bc442cf0cc6f7940dcc567e465048d1a8d634493198c4-sPx5BR_fw236',
    },
    numbers: [],
    recentSales: [],
    comments: [],
    completed: [],
    browser: [],
    cpu: {},
    user: {
      avatar: 'http://img.hb.aicdn.com/bc442cf0cc6f7940dcc567e465048d1a8d634493198c4-sPx5BR_fw236',
    },
  },
  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen(({ pathname }) => {
        if (pathname === '/home' || pathname === '/') {
          dispatch({ type: 'query' })
          dispatch({ type: 'queryWeather' })
        }
      })
    },
  },
  effects: {
    * query ({
      payload,
    }, { call, put }) {
      const { success ,data } = yield call(query, parse(payload))
      if (success) {
        yield put({
          type: 'updateState',
          payload: data,
        })
      }
    },
    * queryWeather ({
      payload = {},
    }, { call, put }) {
      payload.location = 'shenzhen'
      const { success ,data:result }  = yield call(weatherService.query, payload)
      if (success) {
        const data = result.results[0]
        const weather = {
          city: data.location.name,
          temperature: data.now.temperature,
          name: data.now.text,
          icon: `//s5.sencdn.com/web/icons/3d_50/${data.now.code}.png`,
        }
        yield put({
          type: 'updateState',
          payload: {
            weather,
          },
        })
      }
    },
  },
})
