import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import { Button } from 'antd'
import queryString from 'query-string'
import styles from './index.less'

import { Condition } from 'components'

class ConditionShow extends React.Component {
  constructor (props) {
    super(props)
    this.state = { json: {} }
  }

  handleSelectChange = (gender) => {
    this.setState({
    	json: {},
    })
  }
  
  onChange = (data) =>{
	  this.setState({
	      json: data,
	    })
  }
  
  render () {
    const { data } = this.state

    const conditionProps = {
    	      data: {
    	    	    "negate": false,
    	    	    "operators": 0,
    	    	    "leftvalue": null,
    	    	    "function": "=",
    	    	    "rightvalue": null,
    	    	    "rightExactName": "constant",
    	    	    "rightExactType": null,
    	    	    "rightExactText": null,
    	    	    "rightExactLength": -1,
    	    	    "rightExactPrecision": -1,
    	    	    "rightExactIsnull": false,
    	    	    "rightExactMask": null,
    	    	    "conditions": [
    	    	        {
    	    	            "negate": true,
    	    	            "operators": 0,
    	    	            "leftvalue": "excel1",
    	    	            "function": "=",
    	    	            "rightvalue": "excel2",
    	    	            "rightExactName": "constant",
    	    	            "rightExactType": null,
    	    	            "rightExactText": null,
    	    	            "rightExactLength": -1,
    	    	            "rightExactPrecision": -1,
    	    	            "rightExactIsnull": false,
    	    	            "rightExactMask": null,
    	    	            "conditions": null
    	    	        },
    	    	        {
    	    	            "negate": false,
    	    	            "operators": 1,
    	    	            "leftvalue": null,
    	    	            "function": "=",
    	    	            "rightvalue": null,
    	    	            "rightExactName": "constant",
    	    	            "rightExactType": null,
    	    	            "rightExactText": null,
    	    	            "rightExactLength": -1,
    	    	            "rightExactPrecision": -1,
    	    	            "rightExactIsnull": false,
    	    	            "rightExactMask": null,
    	    	            "conditions": [
    	    	                {
    	    	                    "negate": false,
    	    	                    "operators": 0,
    	    	                    "leftvalue": "excel2",
    	    	                    "function": "=",
    	    	                    "rightvalue": null,
    	    	                    "rightExactName": "constant",
    	    	                    "rightExactType": "String",
    	    	                    "rightExactText": "333",
    	    	                    "rightExactLength": -1,
    	    	                    "rightExactPrecision": -1,
    	    	                    "rightExactIsnull": false,
    	    	                    "rightExactMask": null,
    	    	                    "conditions": null
    	    	                },
    	    	                {
    	    	                    "negate": true,
    	    	                    "operators": 2,
    	    	                    "leftvalue": "excel3",
    	    	                    "function": "!=",
    	    	                    "rightvalue": "excel6",
    	    	                    "rightExactName": "constant",
    	    	                    "rightExactType": null,
    	    	                    "rightExactText": null,
    	    	                    "rightExactLength": -1,
    	    	                    "rightExactPrecision": -1,
    	    	                    "rightExactIsnull": false,
    	    	                    "rightExactMask": null,
    	    	                    "conditions": null
    	    	                },
    	    	                {
    	    	                    "negate": false,
    	    	                    "operators": 4,
    	    	                    "leftvalue": "excel3",
    	    	                    "function": ">=",
    	    	                    "rightvalue": "excel4",
    	    	                    "rightExactName": "constant",
    	    	                    "rightExactType": null,
    	    	                    "rightExactText": null,
    	    	                    "rightExactLength": -1,
    	    	                    "rightExactPrecision": -1,
    	    	                    "rightExactIsnull": false,
    	    	                    "rightExactMask": null,
    	    	                    "conditions": null
    	    	                }
    	    	            ]
    	    	        }
    	    	    ]
    	    	}
    	    }
    
    return (<div className="content-inner">
    			<Condition ref="conditionComp"   onChange={this.onChange}/>
    			<br/>
    			json:
    			<br/>
    			<div>{JSON.stringify(this.state.json)}</div>
           </div>
           )
  }
}


export default ConditionShow

