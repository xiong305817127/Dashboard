import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Button } from 'antd'
import queryString from 'query-string'
import styles from './index.less'


const %%MenuName%% = ({ location, dispatch, %%MenuName%% ,app, loading }) => {
  location.query = queryString.parse(location.search)
  const {  data } = %%MenuName%%
  const { isAdmin } = app



  return (
		<div> <h1>%%MenuName%% content panel </h1></div>
  )
}

%%MenuName%%.propTypes = {
  %%MenuName%%: PropTypes.object,
  app: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ %%MenuName%%,app, loading }) => ({ %%MenuName%%,app, loading }))(%%MenuName%%)
