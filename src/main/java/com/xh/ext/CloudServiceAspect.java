/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.ext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpEntity;

import com.xh.common.dto.CommonDto;
import com.xh.common.dto.ReturnCodeDto;

import net.sf.json.JSONObject;

/**
 * Common aspect providing logger & exception
 * procedures for all cloud service implementation.
 * @author JW
 * @since 2017年7月26日
 *
 */
@Aspect
public class CloudServiceAspect {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 服务方法切点（包括com.xh.service.*的全部包， 包括子包）
	 */
//	@Pointcut("execution(* com.xh.service.*.*.*(..))") //不包括子包
	@Pointcut("execution(* com.xh.service..*.*(..))")
	private void servicePointcut() {}
	
	/**
	 * 控制器方法切点（包括com.xh.controller的全部包，包括子包）
	 */
	@Pointcut("execution(* com.xh.controller..*.*(..)) && !@within(org.springframework.web.bind.annotation.ControllerAdvice)")
	private void controllerPointcut() {}
	
	
	/**
	 * 切面：控制器方法调用Before通知
	 * @param joinPoint
	 */
	@Before("controllerPointcut()")
	public void beforeRequest(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		StringBuilder log = new StringBuilder("");
		for (Object arg : args) {
			if(arg!=null ){
				log.append(((arg instanceof CommonDto)?JSONObject.fromObject(arg).toString():arg )+ " ,");
			}else{
				log.append(arg+ " ,");
			}
		}
		logger.info("请求方法：" + className + "." + methodName + "(" + log.toString() + ")");
	}
	
	/**
	 * 切面：控制器方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "controllerPointcut()", returning = "returnVal")
	public void afterResponse(Object returnVal) {
		logger.info("请求响应：" + (returnVal != null ? returnVal.toString() : "<NULL>"));
	}
	
	/**
	 * 切面：控制器方法调用Around通知，包装返回值，增加返回代码
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("controllerPointcut()")
	public Object wrapReturnCode(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object object = proceedingJoinPoint.proceed(); // 返回值用一个Object类型来接收
		if( object instanceof ReturnCodeDto || object instanceof HttpEntity){
			return object;
		}else{
			return  new ReturnCodeDto(0,object);
		}
	}
	
	/**
	 * 切面：服务方法调用Before通知
	 * @param joinPoint
	 */
	@Before("servicePointcut()")
	public void beforeService(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		StringBuilder log = new StringBuilder("");
		for (Object arg : args) {
			log.append(arg + " ");
		}
		logger.debug("服务调用：" + className + "." + methodName + "(" + log.toString() + ")");
	}
	
	/**
	 * 切面：服务方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "servicePointcut()", returning = "returnVal")
	public void afterServiceReturing(Object returnVal) {
		logger.debug("服务结束：" + (returnVal != null ? returnVal.toString() : "<NULL>"));
	}

	/**
	 * 切面：服务方法调用AfterThrowing通知
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "servicePointcut()", throwing="e")
	public void afterThrowingForService(JoinPoint joinPoint, Throwable e) {
		
	}

}
