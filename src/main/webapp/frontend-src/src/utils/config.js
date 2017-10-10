const APIV1 = 'api/v1'
const APIV2 = 'api/v2'

module.exports = {
  name: 'AntD Admin',
  prefix: 'antdAdmin',
  footerText: 'Ant Design Admin  © 2017 zuiidea',
  logo: 'logo.png',
  iconFontCSS: 'iconfont.css',
  iconFontJS: 'iconfont.js',
  CORS: [],
  openPages: ['/login'],
  apiPrefix: '/api/v1',
  APIV1,
  APIV2,
  api: {
    userLogin: `${APIV1}/login`,
    userLogout: `${APIV1}/logout`,
    userInfo: `${APIV1}/userInfo`,
    users: `${APIV1}/users`,
    user: `${APIV1}/user/:id`,
    permissions: `${APIV1}/permissions`,
    permission: `${APIV1}/permission/:role`,
    posts: `${APIV1}/posts`,
    home: `${APIV1}/home`,
    menus: `${APIV1}/menus`,
    menu: `${APIV1}/menu/:id`,
    weather: `${APIV1}/weather`,
    v1test: `${APIV1}/test`,
    v2test: `${APIV2}/test`,
    //dynamic add Api,Do not delete
  },
}
