/**
 * HTTP交互组件
 */
package com.zkhy.http;

import java.util.ResourceBundle;

/**
 * 概要：请求配置类
 * @author yachen
 * @version 1.0.0
 * @date 2014-3-15 
 */
public class ManagerConfig {
	
	/**
	 * 资源配置文件解析器
	 */
	private static ResourceBundle configBundle;

	/**
	 * 最大连接数
	 */
	public static final int MAX_CONNECTION = 2000;

	/**
	 * 路由最大连接数
	 */
	public static final int MAX_PERROUTE = 1000;
	
	/**
	 * 最大连接超时时间:10000ms=10s
	 */
	public static final int MAX_CONNECTION_TIMEOUT = 10000;
	
	/**
	 * Socket超时时间:6000ms=6s
	 */
	public static final int MAX_SOCKET_TIMEOUT = 6000;
	
	/**
	 * key：连接数
	 */
	private static final String KEY_CONNECTION = "connection";
	
	/**
	 * key：路由数
	 */
	private static final String KEY_PREROUTE = "preroute";
	
	/**
	 * key：连接超时
	 */
	private static final String KEY_CONNECT_TIMEOUT = "connection.timeout";
	
	/**
	 * key：socket超时
	 */
	private static final String KEY_SOCKET_TIMEOUT = "socket.timeout";
	
	/**
	 * 连接数
	 */
	private int connection;
	
	/**
	 * 路由数
	 */
	private int preroute;
	
	/**
	 * 连接超时
	 */
	private int connectionTimout;
	
	/**
	 * socket超时
	 */
	private int socketTimeout;
	
	
	/**
	 * 根据配置文件生成配置对象
	 * @param configPath 配置文件路径
	 * @return 配置对象
	 */
	public static ManagerConfig build(String configPath) {
		try {
			configBundle =  ResourceBundle.getBundle(configPath);
		} catch (Exception e) {
			return null;
		}

		ManagerConfig config = new ManagerConfig();
		
		//设置配置的属性
		config.connection = getIntProperty(KEY_CONNECTION, MAX_CONNECTION);
		config.preroute =  getIntProperty(KEY_PREROUTE, MAX_PERROUTE);
		config.connectionTimout = getIntProperty(KEY_CONNECT_TIMEOUT, MAX_CONNECTION_TIMEOUT);
		config.socketTimeout = getIntProperty(KEY_SOCKET_TIMEOUT, MAX_SOCKET_TIMEOUT);
		
		return config;
	}
	
	/**
	 * 根据property文件中的key设置int型的属性，若解析失败或者不符合最大值，则按最大值赋值
	 * @param property
	 * @param key
	 * @param maxValue
	 */
	private static int getIntProperty(String key, int maxValue) {
		String value;
		try {
			value = configBundle.getString(key);
		} catch (Exception e) {
			return maxValue;
		}
		
		int configValue = maxValue;
		try {
			configValue = Integer.parseInt(value);
			if(configValue < maxValue && configValue > 0) {
				return configValue;
			}
		} catch (NumberFormatException e) {
			return maxValue;
		}
		
		return configValue;
	}

	/**
	 * @return the connection
	 */
	public int getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(int connection) {
		this.connection = connection;
	}

	/**
	 * @return the preroute
	 */
	public int getPreroute() {
		return preroute;
	}

	/**
	 * @param preroute the preroute to set
	 */
	public void setPreroute(int preroute) {
		this.preroute = preroute;
	}

	/**
	 * @return the connectionTimout
	 */
	public int getConnectionTimout() {
		return connectionTimout;
	}

	/**
	 * @param connectionTimout the connectionTimout to set
	 */
	public void setConnectionTimout(int connectionTimout) {
		this.connectionTimout = connectionTimout;
	}

	/**
	 * @return the socketTimeout
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}

	/**
	 * @param socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
}
