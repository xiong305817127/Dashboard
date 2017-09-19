import { message } from 'antd'
import dva from 'dva'
import createLoading from 'dva-loading'
//import createHistory from 'history/createBrowserHistory'
import createHistory from 'history/createHashHistory'
import 'babel-polyfill'

// 1. Initialize
const app = dva({
  ...createLoading({
    effects: true,
  }),
  history: createHistory(),
  onError (error) {
    if (error.statusCode == 401  ) {
      app._history.push("/login")
    }else{
      message.error(error.message)
    }

  },
})

// 2. Model
app.model(require('./models/app'))

// 3. Router
app.router(require('./router'))

// 4. Start
app.start('#root')
