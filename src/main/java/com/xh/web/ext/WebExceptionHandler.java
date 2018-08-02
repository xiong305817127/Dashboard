/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.xh.web.ext;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.xh.common.CommonException;
import com.xh.dto.common.ReturnCodeDto;
import com.xh.util.Utils;

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
	public void initBinder(WebDataBinder binder,@SessionAttribute() Object user) throws CommonException {  
		if(Utils.isNull(user)){
			throw new CommonException( "Not Login" );
		}
	}

	/**
	 * Web异常处理 - CommonException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(CommonException.class)
	public @ResponseBody  ReturnCodeDto webExceptionHandler(HttpServletRequest request,CommonException e) {
		ReturnCodeDto returnDto = new ReturnCodeDto(false,e.getMessage());
		logger.debug(" Request URI: " + request.getRequestURI(),e);
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
		ReturnCodeDto returnDto = new ReturnCodeDto(false,CommonException.getExceptionMessage(e),null);
		logger.debug(" Request URI: " + request.getRequestURI(),e);
		logger.info("异常响应：" + returnDto.toString());
		return returnDto;
	}



}
