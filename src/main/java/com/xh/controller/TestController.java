package com.xh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xh.common.controller.BaseController;
import com.xh.dto.ConfigDto;
import com.xh.service.config.ConfigService;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController{
	
	@Autowired
	ConfigService configService;

	
	@RequestMapping(method=RequestMethod.GET, value="/getInfo")
	public @ResponseBody Object getInfo( @RequestParam String key) throws Exception {
		return configService.getStringByKey(key);
	}


	@RequestMapping(method=RequestMethod.POST, value="/saveInfo")
	public @ResponseBody Object saveInfo(@RequestBody ConfigDto config) throws Exception {
		return configService.addConfig(config.getKey(),config.getValue());
	}

}
