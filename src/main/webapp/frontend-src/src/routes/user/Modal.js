import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, InputNumber, Radio, Modal, Cascader,Select } from 'antd'
import city from '../../utils/city'
import { EnumRoleType } from 'enums'

const FormItem = Form.Item

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

const modal = ({
  item = {},
  onOk,
  isAdmin=false,
  modalType="update",
  form: {
    getFieldDecorator,
    validateFields,
    getFieldsValue,
  },
  ...modalProps
}) => {
  const handleOk = () => {
    validateFields((errors) => {
      if (errors) {
        return
      }
      const data = {
        ...getFieldsValue(),
        key: item.key,
      }
      if(data.address ){
        data.address = data.address.join(' ')
      }
      onOk(data)
    })
  }
  const selectOptions = modalProps.permissions.map(d => <Select.Option key={d.id} value={d.id}>{d.role}</Select.Option>)
  const  isShowPermissions = isAdmin && item.permissionId != EnumRoleType.ADMIN

  const modalOpts = {
    ...modalProps,
    onOk: handleOk,
  }

  return (
    <Modal {...modalOpts}>
      <Form layout="horizontal">
        <FormItem label="Name" hasFeedback {...formItemLayout}>
          {getFieldDecorator('username', {
            initialValue: item.username,
            rules: [
              {
                required: true,
              },
            ],
          })(<Input disabled={ modalType === 'create' ?false:true} />)}
        </FormItem>
        { isShowPermissions &&
        <FormItem label="Permission" hasFeedback {...formItemLayout}>
          {getFieldDecorator('permissionId', {
            initialValue: modalType === 'create' ?"developer":item.permissionId ,
            rules: [
              {
                required: true,
              },
            ],
          })(<Select  style={{ width: '100%' }}  >
            {selectOptions}
          </Select>)}
        </FormItem>
        }

        {  modalType === 'create' && <FormItem label="Password" hasFeedback {...formItemLayout}>
          {getFieldDecorator('password', {
            initialValue: item.password,
            rules: [
              {
                required: true,
              },
            ],
          })(<Input type="password"/>)}
        </FormItem>

        }
        <FormItem label="NickName" hasFeedback {...formItemLayout}>
          {getFieldDecorator('nickName', {
            initialValue: item.nickName,
            rules: [
              {
                required: false,
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem label="Gender" hasFeedback {...formItemLayout}>
          {getFieldDecorator('male', {
            initialValue: item.male,
            rules: [
              {
                required: false,
                type: 'boolean',
              },
            ],
          })(
            <Radio.Group>
              <Radio value>Male</Radio>
              <Radio value={false} >Female</Radio>
            </Radio.Group>
          )}
        </FormItem>
        <FormItem label="Age" hasFeedback {...formItemLayout}>
          {getFieldDecorator('age', {
            initialValue: item.age,
            rules: [
              {
                required: false,
                type: 'number',
              },
            ],
          })(<InputNumber min={15} max={100} />)}
        </FormItem>
        <FormItem label="Phone" hasFeedback {...formItemLayout}>
          {getFieldDecorator('phone', {
            initialValue: item.phone,
            rules: [
              {
                required: false,
                pattern: /^1[34578]\d{9}$/,
                message: 'The input is not valid phone!',
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem label="E-mail" hasFeedback {...formItemLayout}>
          {getFieldDecorator('email', {
            initialValue: item.email,
            rules: [
              {
                required: false,
                pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,
                message: 'The input is not valid E-mail!',
              },
            ],
          })(<Input />)}
        </FormItem>
        <FormItem label="Address" hasFeedback {...formItemLayout}>
          {getFieldDecorator('address', {
            initialValue: item.address && item.address.split(' '),
            rules: [
              {
                required: false,
              },
            ],
          })(<Cascader
            size="large"
            style={{ width: '100%' }}
            options={city}
            placeholder="Pick an address"
          />)}
        </FormItem>
      </Form>
    </Modal>
  )
}

modal.propTypes = {
  form: PropTypes.object.isRequired,
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default Form.create()(modal)
