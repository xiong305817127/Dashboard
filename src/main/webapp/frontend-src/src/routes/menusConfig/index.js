import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import {Layout, Row, Col, Tree ,Icon, Button, Popconfirm ,Tabs,Form,Table,Input } from 'antd'
import queryString from 'query-string'
import { arrayToTree, queryArray } from 'utils'
import styles from './index.less'
import EditMenu from './EditMenu'

const TreeNode = Tree.TreeNode;
const {  Sider, Content } = Layout;
const TabPane = Tabs.TabPane;
const FormItem = Form.Item

const Menus = ({ location, dispatch, menusConfig,app, loading }) => {
  location.query = queryString.parse(location.search)
  const {  menu ,currentSelectMenu,modalType,showForm,permissions,currentPermissions,checkedPermissions,showAdd,createTemplate } = menusConfig
  const { isAdmin ,isDev} = app
  const menuTree = arrayToTree(menu.filter(_ => _.mpid !== '-1'), 'id', 'mpid')

  const changeTabs = (key) => {
    if(key == 2 ){
      dispatch({
        type: "menusConfig/queryPermissions",
        payload: {
        },
      })

    }

  }

  const onSelect = (selectedKeys, info) => {

    dispatch({
      type: "menusConfig/updateState",
      payload: {
        currentSelectMenu:{},
        modalType:"update",
        showForm:false
      },
    })

    if(info  && info.selectedNodes && info.selectedNodes[0] && info.selectedNodes[0].props){
      dispatch({
        type: "menusConfig/updateState",
        payload: {
          currentSelectMenu:info.selectedNodes[0].props.dataRef,
          modalType:"update",
          showForm:true
        },
      })
    }
  }

  const onCheck = (checkedKeys, info) => {
    const { halfCheckedKeys } = info ;
    dispatch({
      type: "menusConfig/updateState",
      payload: {
        checkedPermissions:checkedKeys.concat(halfCheckedKeys)
      },
    })

  }

  const openCreate = () =>{
    dispatch({
      type: "menusConfig/updateState",
      payload: {
        currentSelectMenu:{},
        modalType:"create",
        showForm:true
      },
    })
  }

  const onTemplateChange = (value) =>{
    dispatch({
      type: "menusConfig/updateState",
      payload: {
        createTemplate:value
      },
    })
  }

  const onEditOk = (data) =>{
    dispatch({
      type: `menusConfig/${modalType}`,
      payload: data,
    })
  }

  const onDeleteItem = (data) => {
    dispatch({
      type: "menusConfig/delete",
      payload: data,
    })
  }

  const onDeletePermissions = () =>{
    const data={role:currentPermissions.record.role,visit:checkedPermissions}
    dispatch({
      type: "menusConfig/deletePermissions",
      payload: data,
    })
  }

  const onUpdatePermissions = () =>{
    const data={role:currentPermissions.record.role,visit:checkedPermissions}
    dispatch({
      type: "menusConfig/updatePermissions",
      payload: data,
    })
  }

  const onPermissionNameChange = ( e) =>{
    if(e.target.value){
      dispatch({
        type: "menusConfig/updateState",
        payload: {
          showAdd:true,
          currentPermissions:{record:{role:e.target.value,visit:[]}},
          checkedPermissions:[]
        },
      })
    }else{
      dispatch({
        type: "menusConfig/updateState",
        payload: {
          showAdd:true,
          currentPermissions:{},
          checkedPermissions:[]
        },
      })
    }
  }

  const onAddPermissions =() =>{
    const data={role:currentPermissions.record.role,visit:checkedPermissions}
    dispatch({
      type: "menusConfig/addPermissions",
      payload: data,
    })
  }

  const addPermission= () =>{
    dispatch({
      type: "menusConfig/updateState",
      payload: {
        showAdd:true,
        currentPermissions:{},
        checkedPermissions:[]
      },
    })
  }

  const renderTreeNodes = (data) => {
    return data.map((item) => {

     if (item.children) {
        return (
          <TreeNode title={<span  >
          {item.icon && <Icon type={item.icon} className={styles.icon}/>}
          { item.name}
      </span>} key={item.id} dataRef={item}>
          {renderTreeNodes(item.children)}
          </TreeNode>
      );
      }
      return <TreeNode  title={<span >
        {item.icon && <Icon type={item.icon} className={styles.icon} />}
      { item.name}
      </span>}  key={item.id} dataRef={item} />;
    });
  }

  const columns = [
    {
      title: 'Permission',
      dataIndex: 'role',
      key: 'role'
    }
  ]

  const editMenuProps = {
    menu : currentSelectMenu,
    modalType:modalType,
    menuTree:menuTree,
    createTemplate:createTemplate,
    isDev:isDev,
    onOk:onEditOk,
    onDelete:onDeleteItem,
    onTemplateChange:onTemplateChange
  }

  const tableProps ={
    dataSource: permissions,
    pagination:false,
    size:"small",
    rowKey:"role",
    onRowClick:function(record,index,e){

      if( !currentPermissions.target ||currentPermissions.target != e.target){
        if(currentPermissions.target){
          currentPermissions.target.style="background: inherit;";
        }
        e.target.style="background-color:lightblue;"
        dispatch({
          type: "menusConfig/updateState",
          payload: {
            currentPermissions:{record:record,target:e.target},
            checkedPermissions:record.visit?record.visit:[]
          },
        })
      }else{
        currentPermissions.target.style="background: inherit;";
        dispatch({
          type: "menusConfig/updateState",
          payload: {
            currentPermissions:{},
            checkedPermissions:[]
          },
        })
      }

    }
  }

  return (

  <Tabs defaultActiveKey="1" onChange={changeTabs} >
    <TabPane tab="菜单管理" key="1">
      <div className="content-inner">
        <Layout>
          <Sider className={styles.sider}>
            <Tree
              showIcon
              showLine
              onSelect={onSelect}
            >
              {renderTreeNodes(menuTree)}
            </Tree>
          </Sider >
          <Content className={styles.content}>
            { showForm  &&  <EditMenu key={currentSelectMenu.name}  {...editMenuProps} /> }
            { (!showForm&&isDev)  &&
            <div>
              <Button  type="primary"  icon="plus" size="large" ghost  className={styles.addIcon} onClick={openCreate} ></Button>
              <div className={styles.addText} >增加菜单</div>
            </div>
            }
          </Content>
        </Layout>
      </div>
    </TabPane>
    <TabPane tab="权限管理" key="2">
      <div>
        { (currentPermissions && currentPermissions.record) &&
        <Row style={{ marginBottom: 24, textAlign: 'right', fontSize: 13 }} key={currentPermissions.record.role}>
            <Col >
              { (  !showAdd && JSON.stringify(checkedPermissions) != JSON.stringify(currentPermissions.record.visit||[]) )&&
                <Popconfirm key={currentPermissions.record.role} title={'Are you sure update the Permission?'} placement="left"  onConfirm={onUpdatePermissions}>
                  <Button type="primary" size="large" style={{ marginLeft: 8 }}>Update</Button>
                </Popconfirm>
              }
              {
                !showAdd &&
                <Popconfirm title={'Are you sure delete the Permission?'} placement="right" onConfirm={onDeletePermissions}>
                  <Button type="primary" size="large" style={{ marginLeft: 8 }}>Remove</Button>
                </Popconfirm>
              }
              {
                showAdd &&
                <Popconfirm title={'Are you sure add the Permission?'} placement="right" onConfirm={onAddPermissions}>
                  <Button type="primary" size="large" style={{ marginLeft: 8 }}>Add</Button>
                </Popconfirm>
              }

            </Col>

        </Row>
        }
        <Layout>
          <Sider className={styles.sider}>
            <Table
              {...tableProps}
              bordered
              columns={columns}
              simple
              rowKey={"usertable"}
            />
            {
              showAdd &&
              <Input onChange={onPermissionNameChange} placeholder="Please enter a role name" style={{ width: '100%', marginTop: '10px' }} />
            }
            <Button type="dashed" onClick={addPermission} style={{ width: '100%',"marginTop":'10px' }}>
              <Icon type="plus" /> Add Permission
            </Button>
          </Sider >
          { (currentPermissions && currentPermissions.record) &&
          <Content key={currentPermissions.record.role} className={styles.permissionsContent}>
            <Tree
              showIcon
              showLine
              checkable
              defaultExpandAll
              defaultCheckedKeys={currentPermissions.record.visit?currentPermissions.record.visit:[]}
              onCheck={onCheck}
            >
              {renderTreeNodes(menuTree)}
            </Tree>
          </Content>
          }
        </Layout>


      </div>
    </TabPane>

  </Tabs>





  )
}

Menus.propTypes = {
  menusConfig: PropTypes.object,
  app: PropTypes.object,
  location: PropTypes.object,
  dispatch: PropTypes.func,
  loading: PropTypes.object,
}

export default connect(({ menusConfig,app, loading }) => ({ menusConfig,app, loading }))(Menus)
