import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'dva'
import { Button, Row, Form, Input } from 'antd'
import { config } from 'utils'
import styles from './index.less'

const FormItem = Form.Item

const Login = ({
  loading,
  login,
  dispatch,
  form: {
    getFieldDecorator,
    validateFieldsAndScroll,
  },
}) => {

  const { showPassword } = login
  function handleOk () {
    validateFieldsAndScroll((errors, values) => {
      if (errors) {
        return
      }
      dispatch({ type: 'login/login', payload: values })
    })
  }

  const onShowPassword1 = (e) =>{
    dispatch({ type: 'login/updateState', payload: {showPassword : 1} })
  }

  const onShowPassword2 = (e) =>{
    dispatch({ type: 'login/updateState', payload: {showPassword : 2} })
  }

  return (
    <div className={styles.form}>
      <div className={styles.logo}>
        <img alt={'logo'} src={config.logo} />
        <span>{config.name}</span>
      </div>
      <form>
        <FormItem hasFeedback>
          {getFieldDecorator('username', {
            rules: [
              {
                required: true,
              },
            ],
          })(<Input size="large" onPressEnter={handleOk} placeholder="Username" />)}
        </FormItem>
        <FormItem hasFeedback>
          {getFieldDecorator('password', {
            rules: [
              {
                required: true,
              },
            ],
          })(<Input size="large" type="password" onPressEnter={handleOk} placeholder="Password" />)}
        </FormItem>
        <Row>
          <Button type="primary" size="large" onClick={handleOk} loading={loading.effects.login}>
            Sign in
          </Button>
          <p>
            <span>Username：guest</span>
            <span>Password：guest</span>
          </p>
          <p>
            <span>Username：dev-xh</span>
            <span onDoubleClick={onShowPassword2} >Password：{ showPassword==2 ?"123456":"******" }</span>
          </p>
          <p>
            <span>Username：admin</span>
            <span onDoubleClick={onShowPassword1} >Password：{ showPassword==1 ?"123456":"******" }</span>
          </p>
        </Row>

      </form>
    </div>
  )
}

Login.propTypes = {
  form: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
  login: PropTypes.object,
}

export default connect(({ login , loading }) => ({ login, loading }))(Form.create()(Login))
