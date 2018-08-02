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
public class CloudServiceAspect {
	
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 服务方法切点（包括com.xh.service.*的全部包， 包括子包）
	 */
//	@Pointcut("execution(* com.xh.service.*.*.*(..))") //不包括子包
	@Pointcut("execution(* com.xh.service..*.*(..))")
	private void servicePointcut() {}
	
	/**
	 * 切面：服务方法调用Before通知
	 * @param joinPoint
	 */
	@Before("servicePointcut()")
	public void beforeService(JoinPoint joinPoint) {
//		String className = joinPoint.getTarget().getClass().getSimpleName();
//		String methodName = joinPoint.getSignature().getName();
//		Object[] args = joinPoint.getArgs();
//		StringBuilder log = new StringBuilder("");
//		for (Object arg : args) {
//			log.append(arg + " ");
//		}
//		logger.debug("服务调用：" + className + "." + methodName + "(" + log.toString() + ")");
	}
	
	/**
	 * 切面：服务方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "servicePointcut()", returning = "returnVal")
	public void afterServiceReturing(Object returnVal) {
		//logger.debug("服务结束：" + (returnVal != null ? returnVal.toString() : "<NULL>"));
	}

	@Around("servicePointcut()")
	public Object wrapReturnCode(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Long startTime = System.currentTimeMillis();
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();
		
		Long number = CloudLogger.Num.getAndIncrement();
		//服务处理前
		Object[] args = proceedingJoinPoint.getArgs();
		StringBuilder logStart = new StringBuilder("服务调用:"+methodName);
		logStart.append("( ");
		for (Object arg : args) {
			if(arg!=null && arg.getClass().getSuperclass() == Object.class && (arg.getClass().getInterfaces() == null|| arg.getClass().getInterfaces().length ==0)){
				logStart.append((JSONUtils.isObject(arg)?JSONObject.fromObject(arg).toString():arg )+ " ,");
			}else{
				logStart.append(arg+ " ,");
			}
		}
		logStart.append(" )");
		CloudLogger.getInstance().debug(className, number, logStart.toString());
		
		//服务处理
		Object object = proceedingJoinPoint.proceed(); // 返回值用一个Object类型来接收
		
		//服务处理后
		StringBuilder logEnd = new StringBuilder("服务结束:");
		logEnd.append(",耗时:"+(System.currentTimeMillis() - startTime)+"ms");
		logEnd.append(object != null ? CloudLogUtils.jsonLog2(object) : "<NULL>");
		CloudLogger.getInstance().debug(className, number, logEnd.toString());
		
		return object;
	}
	
	/**
	 * 切面：服务方法调用AfterThrowing通知
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "servicePointcut()", throwing="e")
	public void afterThrowingForService(JoinPoint joinPoint, Throwable e) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		CloudLogger.getInstance().error(className, "异常拦截: 服务方法["+methodName+"]抛出[ "+e.getClass().getSimpleName()+"]异常.",e);
	}

}
