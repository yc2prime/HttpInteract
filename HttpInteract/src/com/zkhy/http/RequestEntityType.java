/**
 * HTTP交互组件
 */
package com.zkhy.http;

/**
 * 概要：请求Entity类型
 * @author yachen
 * @version 2.0.0
 * @date 2014-4-6 
 */
public enum RequestEntityType {
	
	/**
	 * URL请求
	 * <p>是String请求的实现</p>
	 */
	URL,
	
	/**
	 *  文件请求
	 */
	FILE,
	
	/**
	 * 输入流请求
	 */
	INPUT_STREAM,
	
	/**
	 * BYTE数组
	 */
	BYTE_ARRAY,
	
	/**
	 * 分块
	 */
	MULTIPART,

}
