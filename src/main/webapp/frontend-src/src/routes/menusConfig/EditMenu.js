import React from 'react'
import PropTypes from 'prop-types'
import { Form, Input, Button, Select,Tag,Popconfirm ,TreeSelect,Icon} from 'antd'
import { EnumRoleType } from 'enums'
import styles from './index.less'

const FormItem = Form.Item

const TreeNode = TreeSelect.TreeNode;

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

const editMenu = ({
  menu = {},
  isAdmin=false,
  modalType="update",
  onOk ,
  onDelete,
  menuTree,
  form: {
    getFieldDecorator,
    validateFields,
    getFieldsValue,
    getFieldsError,
    submit
    },
  ...editMenuProps
}) => {

  let createId = [];
  const hasErrors = (fieldsError) => {
    return Object.keys(fieldsError).some(field => fieldsError[field]);
  }
  const submitForm = (e) => {
     submit(handleOk);
  }
  const handleOk = () => {
    validateFields((errors) => {
      if (errors) {
        return
      }
      let newMenu = getFieldsValue();
      let newid=menu.id
      if( modalType === 'create'){
        if(newMenu.mpid){
          newid=newMenu.mpid+""+(createId[newMenu.mpid]+1);
        }else{
          newid=createId[-1]+1;
        }
      }
      const data = {
        ...newMenu,
        id: newid
      }
      onOk(data)
    })
  }

  const deleteItem = () =>{
    const data = {
      ...getFieldsValue(),
      id: menu.id
    }
    onDelete(data)
  }

  const onTreeChange = (value,pro) =>{
    if(pro.props.dataRef && pro.props.dataRef.children){
      return false ;
    }
    return true ;
  }

  const renderTreeNodes = (data,pid) => {
    return data.map((item,index) => {

      if(!pid){
        pid=-1;
      }
      createId[pid]=index+1;

      if (item.children) {
        return (
          <TreeNode title={<span  >
          {item.icon && <Icon type={item.icon} className={styles.icon}/>}
          { item.name}
      </span>} key={item.id} value={item.id} dataRef={item}>
            {renderTreeNodes(item.children,item.id)}
          </TreeNode>
        );
      }
      return <TreeNode  title={<span >
        {item.icon && <Icon type={item.icon} className={styles.icon} />}
      { item.name}
      </span>}  key={item.id} value={item.id} dataRef={item}  disabled={item.route?true:false}/>;
    });
  }

  return (
       <Form layout="horizontal" onSubmit={handleOk}>
        <FormItem label="Name" hasFeedback {...formItemLayout}>
          {getFieldDecorator('name', {
            initialValue: menu.name,
            rules: [
              {
                required: true,
              },
            ],
          })(<Input key={ "name"} />)}
        </FormItem>
        <FormItem label="Icon" hasFeedback {...formItemLayout}>
        {getFieldDecorator('icon', {
          initialValue: menu.icon,
          rules: [
            {
              required: true,
            },
          ],
        })(<Input  key={ "icon"} />)}
          <a  href="https://ant.design/components/icon-cn/" target="_blank" className={styles.iconFind} >查找</a>

        </FormItem>
         <FormItem label="Route" hasFeedback {...formItemLayout}>
          {getFieldDecorator('route', {
            initialValue: menu.route,
            rules: [
              {
                required: false,
              },
            ],
          })(<Input key={ "route"} />)}
        </FormItem>

         {  modalType === 'create' && <FormItem label="Parent" hasFeedback {...formItemLayout}>
           {getFieldDecorator('mpid', {
             initialValue: menu.mpid,
             rules: [
               {
                 required: false,
               },
             ],
           })(
             <TreeSelect
               showSearch
               dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
               placeholder="Please select"
               allowClear
               onSelect={onTreeChange}
             >
               {renderTreeNodes(menuTree)}
             </TreeSelect>


           )}
         </FormItem>

         }

         <FormItem  wrapperCol={{ span: 14, offset: 6 }}>
           <div>
               <Popconfirm title={`Are you sure ${modalType} these items?`}  placement="left" onConfirm={submitForm}>
                 <Button type="primary" size="large"  className={styles.btn} >{modalType}</Button>
               </Popconfirm>
             { modalType === 'update' &&  <Popconfirm title={`Are you sure delete these items?`}  placement="right" onConfirm={deleteItem}>
                 <Button type="primary" size="large" >Delete</Button>
               </Popconfirm> }
           </div>
         </FormItem>


      </Form>
  )
}

editMenu.propTypes = {
  form: PropTypes.object.isRequired,
  modalType: PropTypes.string,
  menu: PropTypes.object,
  onOk: PropTypes.func,
}

export default Form.create()(editMenu)
