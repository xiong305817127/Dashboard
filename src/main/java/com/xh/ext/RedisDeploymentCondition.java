/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.ext;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.xh.util.ConfigPropertyUtil;


/**
 * DubboDeploymentCondition.java
 * @author JW
 * @since 2017年8月1日
 *
 */
public class RedisDeploymentCondition implements Condition {
	
	/*
	 * @see org.springframework.context.annotation.Condition#matches(org.springframework.context.annotation.ConditionContext, org.springframework.core.type.AnnotatedTypeMetadata)
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return ConfigPropertyUtil.getBooleanProperty("redis.cache.enable", false);
	}

}
