import React from 'react'
import PropTypes from 'prop-types'
import { Table, Modal } from 'antd'
import classnames from 'classnames'
import { DropOption } from 'components'
import { Link } from 'react-router-dom'
import queryString from 'query-string'
import AnimTableBody from 'components/DataTable/AnimTableBody'
import styles from './List.less'
import { EnumRoleType } from 'enums'

const confirm = Modal.confirm
const tip = tip

const List = ({ onDeleteItem, onEditItem, isMotion, location, ...tableProps }) => {
  location.query = queryString.parse(location.search)

  let isAdmin=tableProps.isAdmin

  const handleMenuClick = (record, e) => {
    if (e.key === '1') {
      onEditItem(record)
    } else if (e.key === '2') {
      if(! isAdmin ){
        tip({
          title:"You have no permissions!",
          type:"error"
        })
        return
      }
      if( record.username == "admin"){
        tip({
          title:"The administrator cannot delete!",
          type:"warning"
        })
        return ;
      }

      confirm({
        title: 'Are you sure delete this record?',
        onOk () {
          onDeleteItem(record.id)
        },
      })
    }
  }

  const columns = [
    {
      title: 'Avatar',
      dataIndex: 'avatar',
      key: 'avatar',
      width: 64,
      className: styles.avatar,
      render: text => <img alt={'avatar'} width={24} src={text} />,
    }, {
      title: 'Name',
      dataIndex: 'username',
      key: 'username',
      render: (text, record) => <Link to={`user/${record.id}`}>{text}</Link>,
    }, {
    title: 'Permission',
      dataIndex: 'permissionId',
      key: 'permissionId',
  }, {
      title: 'NickName',
      dataIndex: 'nickName',
      key: 'nickName',
    }, {
      title: 'Age',
      dataIndex: 'age',
      key: 'age',
    }, {
      title: 'Gender',
      dataIndex: 'male',
      key: 'male',
      render: text => (<span>{text
        ? '男'
        : '女'}</span>),
    } ,{
      title: 'Address',
      dataIndex: 'address',
      key: 'address',
    }, {
      title: 'CreateTime',
      dataIndex: 'createTime',
      key: 'createTime',
    }, {
      title: 'Operation',
      key: 'operation',
      width: 100,
      render: (text, record) => {
        let opt = []
        if(isAdmin && record.permissionId === EnumRoleType.ADMIN){
           opt = [{ key: '1', name: 'Update' }]
        }else if(isAdmin){
          opt = [{ key: '1', name: 'Update' }, { key: '2', name: 'Delete' }]
        }
        return <DropOption onMenuClick={e => handleMenuClick(record, e)} menuOptions={opt} />
      },
    },
  ]

  const getBodyWrapperProps = {
    page: location.query.page,
    current: tableProps.pagination.current,
  }

  const getBodyWrapper = (body) => { return isMotion ? <AnimTableBody {...getBodyWrapperProps} body={body} /> : body }

  return (
    <div>
      <Table
        {...tableProps}
        className={classnames({ [styles.table]: true, [styles.motion]: isMotion })}
        bordered
        scroll={{ x: 1250 }}
        columns={columns}
        simple
        rowKey={record => record.id}
        getBodyWrapper={getBodyWrapper}
      />
    </div>
  )
}

List.propTypes = {
  onDeleteItem: PropTypes.func,
  onEditItem: PropTypes.func,
  isMotion: PropTypes.bool,
  location: PropTypes.object,
}

export default List
