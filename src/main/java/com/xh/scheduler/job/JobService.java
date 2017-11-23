package com.xh.scheduler.job;

/**
 * job服务父类，用来保存获取到的数据，<br>
 * 服务方法定义：<br>
 * Object saveMethod(list l,T t)<br>
 * 服务方法名为SchedulerVo对象的saveMethod值，<br>
 * 参数为 ( 获取到的数据List，获取数据的参数封装对象) ,<br>
 * 返回对象，如果为整型则为 影响到的数据库行数，会存入数据库<br>
 *        如果为非空对象，则为 子查询的父参数，推荐List<br>
 * 
 * 
 * @author xh
 * @date 2017年11月3日 下午7:26:20
 */
public abstract class JobService {
	/**
	 * 获取job服务类型，要和相应的job实现的类型一致
	 * @Title: getJobType 
	 * @return
	 */
	public abstract String getJobType() ;

}
