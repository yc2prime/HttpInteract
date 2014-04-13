/**
 * HTTP交互组件
 */
package com.zkhy.http;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * 概要：HTTP请求管理器
 * @author Yachen ZHU
 * @version 2.0.0
 * @date 2014-1-25 
 */
public class RequestManager {
	
	/**
	 * 连接池管理器
	 */
	private static PoolingHttpClientConnectionManager cm;

	/**
	 * 请求管理器
	 */
	private static RequestManager rm;
	
	/**
	 * 上下文管理
	 */
	private static HttpClientContext context;
	
	/**
	 * 请求配置
	 */
	private static RequestConfig globalConfig;
	
	/**
	 * 客户端实例
	 */
	private static CloseableHttpClient httpClient;
	
	/**
	 * Cookie存储
	 */
	private static CookieStore cookieStore;
	
	/**
	 * RequestManger配置
	 */
	private static ManagerConfig managerConfig;
	
	/**
	 * 日志
	 */
	private static Log logger;
	
	
	/**
	 * 构造函数私有化
	 */
	private RequestManager() {
		//获取连接管理器
		if(cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			
			//设置最大连接数
		    cm.setMaxTotal(ManagerConfig.MAX_CONNECTION);
		    
		    //设置每个路由基础的连接
		    cm.setDefaultMaxPerRoute(ManagerConfig.MAX_PERROUTE);
		
			//生成请求配置
			globalConfig = RequestConfig.custom()
							.setSocketTimeout(ManagerConfig.MAX_SOCKET_TIMEOUT)
							.setConnectTimeout(ManagerConfig.MAX_CONNECTION_TIMEOUT)
							.setCookieSpec(CookieSpecs.BEST_MATCH)
							.build();
	
			//生成客户端
			httpClient = HttpClients.custom()
				            .setConnectionManager(cm)
				            .setDefaultRequestConfig(globalConfig)
				            .build();
			
			//生成Cookie Store
		    cookieStore = new BasicCookieStore();
			
			//获取上下文管理
			context = HttpClientContext.create();
		    context.setCookieStore(cookieStore);
		
			logger = LogFactory.getLog(RequestManager.class);
		}
		
	}
	
	/**
	 * 配置
	 */
	public void config(ManagerConfig config) {
		if(config == null) {
			return;
		}
		
		managerConfig = config;
		
		//设置最大连接数
	    cm.setMaxTotal(config.getConnection());
	    
	    //设置每个路由基础的连接
	    cm.setDefaultMaxPerRoute(config.getPreroute());
	    
		//生成请求配置
		globalConfig = RequestConfig.custom()
						.setSocketTimeout(config.getSocketTimeout())
						.setConnectTimeout(config.getConnectionTimout())
						.setCookieSpec(CookieSpecs.BEST_MATCH)
						.build();

		//生成客户端
		httpClient = HttpClients.custom()
			            .setConnectionManager(cm)
			            .setDefaultRequestConfig(globalConfig)
			            .build();
	    
	}
	
	/**
	 * 获取RequestManager实例
	 * @return RequestManager
	 */
	public static RequestManager getInstance() {
		//Double check
		if(rm == null) {
			synchronized(RequestManager.class)   
            {  
                if (rm== null)  
                {  
                	rm= new RequestManager();  
                }  
            }  
		}
		
		return rm;
	}
	
	/**
	 * 发送请求
	 * @param requestObj 请求对象
	 * @return HTTP响应
	 */
	public CloseableHttpResponse sendRequest(RequestObject reqObj) {
	    //针对不同的请求类型分别进行发送
	    CloseableHttpResponse response = null;
	    
	    
	    String url = reqObj.toUrl();
	    
	    try {
		    //GET 请求
		    if(reqObj.getRequestType() == RequestType.GET) {
		    	HttpGet httpGet = new HttpGet(url);
		    	httpGet.setConfig(globalConfig);
				response = httpClient.execute(httpGet, context);
		    }
		    //POST 请求
		    else if (reqObj.getRequestType() == RequestType.POST) {
		    	HttpPost httpPost = new HttpPost(url);
		    	
		    	httpPost.setConfig(globalConfig);
		    	
		    	httpPost.setEntity(reqObj.buildRequestEntity());
		    	
				response = httpClient.execute(httpPost, context);
			}
		    //PUT 请求
		    else if (reqObj.getRequestType() == RequestType.PUT) {
		    	HttpPost httpPut = new HttpPost(url);
		    	
		    	httpPut.setConfig(globalConfig);
		    	
		    	//目前大部分框架不支持PUT请求，使用_method调用
		    	reqObj.putParam(RequestConstants.STR_METHOD_NAME, RequestType.PUT.toString());
		    	httpPut.setEntity(reqObj.buildRequestEntity());
		    	
				response = httpClient.execute(httpPut, context);
			}
		    //DELETE请求
		    else if (reqObj.getRequestType() == RequestType.DELETE) {
				HttpDelete httpDelete = new HttpDelete(url);
				
				httpDelete.setConfig(globalConfig);
				
		    	//目前大部分框架不支持PUT请求，使用_method调用
		    	reqObj.putParam(RequestConstants.STR_METHOD_NAME, RequestType.DELETE.toString());
				response = httpClient.execute(httpDelete, context);
			}
		    
		} catch (ClientProtocolException e) {
			logger.error(RequestConstants.MSG_CLIENT_PROTOCOL_ERROR + e.toString());
		} catch (IOException e) {
			logger.error(RequestConstants.MSG_CLIENT_IO_ERROR + e.toString());
		}
	    
		return response;
	}
	
	/**
	 * 获取响应字符串
	 * @param response 响应对象
	 * @return 响应字符串
	 */
	public static String parseResponse(CloseableHttpResponse response) {
		//响应内容
		String content = null;
		
		if(response == null) {
			return content;
		}
		
		//只有响应OK时才进行解析
		int responseCode = response.getStatusLine().getStatusCode();
		if(responseCode == HttpStatus.SC_OK) {
			try {
				content = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				logger.error(RequestConstants.MSG_RESPONSE_PARSE_ERROR + e.toString());
			} catch (IOException e) {
				logger.error(RequestConstants.MSG_RESPONSE_IO_ERROR + e.toString());
			}
		}
		else{
			//响应不正常时写入日志
			logger.error(responseCode);
		}
		
		try {
			//关闭相应，释放连接
			response.close();
		} catch (IOException e) {
			logger.error(RequestConstants.MSG_RESPONSE_CLOSE_FAILD + e.toString());
		}
		return content;
	}
	
	
	/**
	 * @return the managerConfig
	 */
	public static ManagerConfig getManagerConfig() {
		return managerConfig;
	}

	/**
	 * 获取日志对象
	 * @return 日志对象
	 */
	public Log getLogger() {
		return logger;
	}
	
}
