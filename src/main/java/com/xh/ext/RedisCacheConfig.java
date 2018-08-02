/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.xh.ext;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.xh.util.ConfigPropertyUtil;
import com.xh.util.RedisUtil;
import com.xh.util.Utils;

import redis.clients.jedis.JedisPoolConfig;

/**
 * CloudDubboConfig.java
 * 
 * @author JW
 * @since 2017年7月27日
 *
 */
@Configuration
public class RedisCacheConfig  {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedisCacheConfig.class);

	/**
	 * 配置JedisPoolConfig实例
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public JedisPoolConfig poolConfig() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.valueOf(ConfigPropertyUtil.getProperty("redis.maxIdle", "300").trim()));
		poolConfig.setMaxTotal(Integer.valueOf(ConfigPropertyUtil.getProperty("redis.maxActive", "600").trim()));
		poolConfig.setMaxWaitMillis(Integer.valueOf(ConfigPropertyUtil.getProperty("redis.maxWait", "1000").trim()));
		poolConfig.setTestOnBorrow(Boolean.valueOf(ConfigPropertyUtil.getProperty("redis.testOnBorrow", "true").trim()));
		LOGGER.debug("Redis - poolConfig()");
		return poolConfig;
	}

	/**
	 * 配置JedisConnectionFactory
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig poolConfig) {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(ConfigPropertyUtil.getProperty("redis.host"));
		jedisConnectionFactory.setPort(Integer.valueOf(ConfigPropertyUtil.getProperty("redis.port", "6379").trim()));
		String pass = ConfigPropertyUtil.getProperty("redis.pass");
		if (!Utils.isEmpty(pass)) {
			jedisConnectionFactory.setPassword(pass);
		}
		jedisConnectionFactory.setDatabase(Integer.valueOf(ConfigPropertyUtil.getProperty("redis.dbIndex", "1").trim()));
		jedisConnectionFactory.setPoolConfig(poolConfig);
		LOGGER.debug("Redis - jedisConnectionFactory()");
		return jedisConnectionFactory;
	}

	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public StringRedisSerializer keySerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public GenericJackson2JsonRedisSerializer valueSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	/**
	 * 配置RedisTemplate
	 * 
	 * @return
	 */
	@Bean
	@Conditional(RedisDeploymentCondition.class)
	public RedisTemplate<String, Object> redisTemplate(SimpleCacheManager cacheManager ,JedisConnectionFactory jedisConnectionFactory,
			StringRedisSerializer keySerializer, GenericJackson2JsonRedisSerializer valueSerializer) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.setKeySerializer(keySerializer);
		redisTemplate.setValueSerializer(valueSerializer);
		redisTemplate.setHashKeySerializer(keySerializer);
		redisTemplate.setHashValueSerializer(valueSerializer);
		redisTemplate.setEnableTransactionSupport(true);

		RedisUtil.setRedisTemplate(redisTemplate);
		
		RedisCache cache = new RedisCache("default", null, redisTemplate, Long.valueOf(ConfigPropertyUtil.getProperty("redis.expiration", "0").trim()), false); 
		cacheManager.setCaches(Arrays.asList(cache));
		cacheManager.initializeCaches();
		
		LOGGER.debug("Redis - redisTemplate()");
		return redisTemplate;
	}

//	/**
//	 * 配置RedisCacheManager
//	 * 
//	 * @return
//	 */
//	@Bean
//	@Conditional(RedisDeploymentCondition.class)
//	public CacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
//		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
//		redisCacheManager.setDefaultExpiration(Long.valueOf(ConfigPropertyUtil.getProperty("redis.expiration", "0").trim()));
//		LOGGER.debug("Redis - redisCacheManager()");
//		return redisCacheManager;
//
//	}
//
//	/**
//	 * @EnableCaching 注解需要使用 CacheManager,当没有配置redis时,给以默认的缓存管理器
//	 * @return
//	 */
//	@Bean
//	@Conditional(RedisNonDeploymentCondition.class)
//	public CacheManager defaultCacheManager() {
//		SimpleCacheManager cacheManager = new SimpleCacheManager();
//		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("sampleCache")));
//		return cacheManager;
//	}

	/**
	 * 通过spring管理redis缓存配置
	 * 

	 * 
	 * @author Administrator
	 *
	 */
//	@Bean
//	@Conditional(RedisDeploymentCondition.class)
//	public CachingConfigurerSupport springCacheConfig(CacheManager cacheManager) {
//		LOGGER.debug("Redis - redisCacheConfig()");
//		return new CachingConfigurerSupport() {
//			@Override
//			public CacheManager cacheManager() {
//				return cacheManager;
//			}
//
//			@Override
//			public KeyGenerator keyGenerator() {
//				return new KeyGenerator() {
//					@Override
//					public Object generate(Object target, Method method, Object... objects) {
//						StringBuilder sb = new StringBuilder();
//						sb.append(target.getClass().getName());
//						sb.append(method.getName());
//						for (Object obj : objects) {
//							sb.append(obj.toString());
//						}
//						return sb.toString();
//					}
//				};
//			}
//		};
//	}


}
