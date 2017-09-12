/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.xh.ext;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xh.common.dto.ReturnCodeDto;
import com.xh.common.exception.WebException;

/**
 * 控制器异常处理切面
 * Common exception handler for all cloud controllers.
 * @author JW
 * @since 2017年7月27日
 *
 */
@ControllerAdvice
public class WebExceptionHandler {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@InitBinder  
	public void initBinder(WebDataBinder binder) {  
		//TODO init
	}

	/**
	 * Web异常处理 - WebException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(WebException.class)
	public @ResponseBody  ReturnCodeDto webExceptionHandler(HttpServletRequest request,WebException e) {
		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setCode(-1);
		returnDto.setMessage(e.getMessage());
		logger.debug(" Request URI: " + request.getRequestURI());
		logger.info("异常响应：" + returnDto.toString());
		return returnDto;
	}


	/**
	 * 通用异常处理 - Exception
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public  @ResponseBody   ReturnCodeDto exceptionHandler(HttpServletRequest request, Exception e) {
		ReturnCodeDto returnDto = new ReturnCodeDto();
		returnDto.setCode(-1);
		returnDto.setMessage(WebException.getExceptionMessage(e));
		logger.debug(" Request URI: " + request.getRequestURI());
		logger.info("异常响应：" + returnDto.toString());
		return returnDto;
	}



}
