package com.xh.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xh.common.CommonException;
import com.xh.dao.jsondatasource.jsonDatabase.JsonDataSource;
import com.xh.util.Utils;
import com.xh.web.common.BaseController;

import net.sf.json.JSONArray;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class HomeController extends BaseController{
	
	@Autowired
	JsonDataSource datasource ;
	
	@RequestMapping(method=RequestMethod.GET,value="/home")
	public @ResponseBody Object getHomeInfo() throws CommonException {
		JSONArray res = datasource.getTableFile("home");
		if(res.size() >0){
			return res.get(0);
		}
		return "";
	}
	
}
