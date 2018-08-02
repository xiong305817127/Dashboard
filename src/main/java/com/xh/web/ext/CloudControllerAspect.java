/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.web.ext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpEntity;

import com.xh.dto.common.CommonDto;
import com.xh.dto.common.ReturnCodeDto;
import com.xh.logger.CloudLogUtils;
import com.xh.logger.CloudLogger;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * Common aspect providing logger & exception
 * procedures for all cloud service implementation.
 * @author JW
 * @since 2017年7月26日
 *
 */
@Aspect
public class CloudControllerAspect {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 控制器方法切点（包括com.xh.controller的全部包，包括子包）
	 */
	@Pointcut("execution(* com.xh.web.controller..*.*(..)) && !@within(org.springframework.web.bind.annotation.ControllerAdvice)")
	private void controllerPointcut() {}
	
	
	/**
	 * 切面：控制器方法调用Before通知
	 * @param joinPoint
	 */
	@Before("controllerPointcut()")
	public void beforeRequest(JoinPoint joinPoint) {
//		String className = joinPoint.getTarget().getClass().getSimpleName();
//		String methodName = joinPoint.getSignature().getName();
//		Object[] args = joinPoint.getArgs();
//		StringBuilder log = new StringBuilder("");
//		for (Object arg : args) {
//			if(arg!=null ){
//				log.append(((arg instanceof  CommonDto)?JSONObject.fromObject(arg).toString():arg )+ " ,");
//			}else{
//				log.append(arg+ " ,");
//			}
//		}
//		logger.info("请求方法：" + className + "." + methodName + "(" + log.toString() + ")");
	}
	
	/**
	 * 切面：控制器方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "controllerPointcut()", returning = "returnVal")
	public void afterResponse(Object returnVal) {
		//logger.info("请求响应：" + (returnVal != null ? returnVal.toString() : "<NULL>"));
	}
	
	/**
	 * 切面：控制器方法调用Around通知，包装返回值，增加返回代码
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("controllerPointcut()")
	public Object wrapReturnCode(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Long startTime = System.currentTimeMillis();
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();

		Long number = CloudLogger.Num.getAndIncrement();
		//控制器处理前
		Object[] args = proceedingJoinPoint.getArgs();
		StringBuilder logStart = new StringBuilder("请求:"+methodName);
		logStart.append("( ");
		for (Object arg : args) {
			if(arg!=null && arg.getClass().getSuperclass() == Object.class && (arg.getClass().getInterfaces() == null|| arg.getClass().getInterfaces().length ==0)){
				logStart.append((JSONUtils.isObject(arg)?JSONObject.fromObject(arg).toString():arg )+ " ,");
			}else{
				logStart.append(arg+ " ,");
			}
		}
		logStart.append(" )");
		CloudLogger.getInstance().info(className,number,logStart.toString() );
		
		//控制器处理
		Object object = proceedingJoinPoint.proceed(); // 返回值用一个Object类型来接收
		
		//控制器处理后
		Object result;
		if( object instanceof ReturnCodeDto || object instanceof HttpEntity){
			result = object;
		}else{
			result = new ReturnCodeDto(true,object);
		}
		StringBuilder logEnd = new StringBuilder("响应: ");
		logEnd.append("耗时:"+(System.currentTimeMillis() - startTime)+"ms ");
		logEnd.append(",result: "+ (result != null ? CloudLogUtils.jsonLog2(result) : "<NULL>") );
		CloudLogger.getInstance().info(className,number,logEnd.toString());
		
		return result;
		
		
		
		
		
		
		
		
		
	}
	
	

}
