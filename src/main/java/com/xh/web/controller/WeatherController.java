package com.xh.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xh.common.CommonException;
import com.xh.util.Utils;
import com.xh.util.http.HttpUtils;
import com.xh.web.common.BaseController;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(Utils.API_PREFIX)
public class WeatherController extends BaseController{
	
	@RequestMapping(method=RequestMethod.GET,value="/weather/now.json")
	public @ResponseBody Object getWeather(@RequestParam("location") String location ,@RequestParam("key") String key ) throws  CommonException {
		return JSONObject.fromObject(HttpUtils.doGet("https://api.seniverse.com/v3/weather/now.json", Utils.newHashMap("location",location,"key",key), null));
	}
	
}
