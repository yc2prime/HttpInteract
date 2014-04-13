/**
 * HTTP交互组件
 */
package com.zkhy.http;

/**
 * 概要：请求使用的常量
 * @author yc2prime
 * @version 1.0.0
 * @date 2014-1-25 
 */
public class RequestConstants {

	/**
	 * utf-8编码标识
	 */
	public static final String ENCODING_UTF_8 = "utf-8";

	/**
	 * GB-2312编码标识
	 */
	public static final String ENCODING_GB2312 = "gb2312";
	
	/**
	 * 需要调用的方法名称
	 */
	public static final String STR_METHOD_NAME = "_method";
	
	/**
	 * 不支持的编码格式
	 */
	public static final String MSG_UNSUPPORTED_ENCODING = "Unsupported coding format ";
	
	/**
	 * 连接关闭失败
	 */
	public static final String MSG_RESPONSE_CLOSE_FAILD = "Response close failed ";
	
	/**
	 * 响应解析错误
	 */
	public static final String MSG_RESPONSE_PARSE_ERROR = "Response parsing failed ";
	
	/**
	 * 响应IO错误
	 */
	public static final String MSG_RESPONSE_IO_ERROR = "Response I/O error ";
	
	
	/**
	 * URL构建错误
	 */
	public static final String MSG_URL_BUILD_ERROR = "URL building failed ";
	

	/**
	 * 客户端协议错误
	 */
	public static final String MSG_CLIENT_PROTOCOL_ERROR = "Client protocol error ";
	

	/**
	 * 客户端IO错误
	 */
	public static final String MSG_CLIENT_IO_ERROR = "Client I/O error ";

}
