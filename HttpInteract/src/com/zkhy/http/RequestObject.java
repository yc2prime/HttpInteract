/**
 * HTTP交互组件
 */
package com.zkhy.http;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * 概要：HTTP请求对象
 * @author yc2prime
 * @version 2.0.0
 * @date 2014-1-25 
 */
public class RequestObject {
	
	/**
	 * 日志
	 */
	private static Log logger;
	
	/**
	 * 构造函数
	 */
	public RequestObject() {
		if(logger == null) {
			logger = LogFactory.getLog(RequestObject.class);
		}
		
		//默认编码
		encoding = RequestConstants.ENCODING_UTF_8;
	}

	/**
	 * 协议类型
	 */
	private String scheme;
	
	/**
	 * 内容类型
	 */
	private ContentType contentType;
	
	/**
	 * 请求主机
	 */
	private String host;
	
	/**
	 * 参数列表
	 */
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	
	/**
	 * 请求类型
	 */
	private RequestType requestType;
	
	/**
	 * 端口
	 */
	private int port;

	/**
	 * 路径
	 */
	private String path;
	
	/**
	 * 编码格式
	 */
	private String encoding;
	
	/**
	 * 文件
	 */
	private File requestFile;
	
	/**
	 * 输入流
	 */
	private InputStream requestInputStream;
	
	/**
	 * byte数组
	 */
	private byte[] requestByteArray;
	
	/**
	 * 请求实体类型
	 */
	private RequestEntityType requestEntityType;
	
	/**
	 * 分块类型构造器
	 */
	private MultipartEntityBuilder multipartEntityBuilder;
	
	
	/**
	 * 获取URL
	 * @return URL
	 */
	public String toUrl() {
		URI uri = null;
		
		try {
			URIBuilder uriBuilder = new URIBuilder()
					.setScheme(scheme).setHost(host).setPort(port)
					.setPath(path);
			
			if(requestType == RequestType.GET | 
					requestType == RequestType.DELETE) {
				uriBuilder.addParameters(params);
			}
			
			uri = uriBuilder.build();
			
		} catch (URISyntaxException e) {
			logger.error(RequestConstants.MSG_URL_BUILD_ERROR + e.toString());
		}
		
		return uri.toString();
	}


	/**
	 * 添加/更新参数
	 * @param name 参数键
	 * @param value 参数的值
	 * @return RequestObject对象自身，方便链式调用
	 * <p>
	 * 	例如：
	 *  <p>RequestObject obj = new RequestObject();</p>
	 *  <p>obj.putParam("name", "xxx").putParam("pwd", "password");</p>
	 * </p>
	 */
	public RequestObject putParam(String name, String value) {
		if(multipartEntityBuilder == null 
				&& requestEntityType != RequestEntityType.MULTIPART) {
			params.add(new BasicNameValuePair(name, value));
		}
		else {
			multipartEntityBuilder.addTextBody(name, value, 
					contentType.withCharset(Charset.forName(encoding)));
		}
		return this;
	}
	
	
	/**
	 * 添加/更新参数
	 * <p><b>如果不是分块类型的请求，则无法添加除字符串类型以外的参数</b></p>
	 * @param name
	 * @param requestFile
	 * @return
	 */
	public RequestObject putParam(String name, File requestFile) {
		if(this.multipartEntityBuilder == null) {
			return this;
		}
		
		this.multipartEntityBuilder.addBinaryBody(
				name, requestFile, contentType, requestFile.getName());
		
		return this;
	}
	
	/**
	 * 添加/更新参数
	 * <p><b>如果不是分块类型的请求，则无法添加除字符串类型以外的参数</b></p>
	 * @param name
	 * @param requestInputStream
	 * @return
	 */
	public RequestObject putParam(String name, InputStream requestInputStream) {
		if(this.multipartEntityBuilder == null) {
			return this;
		}
		
		this.multipartEntityBuilder.addBinaryBody(name, requestInputStream);
		
		return this;
	}
	
	/**
	 * 添加/更新参数
	 * <p><b>如果不是分块类型的请求，则无法添加除字符串类型以外的参数</b></p>
	 * @param name
	 * @param requestByteArray
	 * @return
	 */
	public RequestObject putParam(String name, byte[] requestByteArray) {
		if(this.multipartEntityBuilder == null) {
			return this;
		}
		
		this.multipartEntityBuilder.addBinaryBody(name, requestByteArray);
		
		return this;
	}
	
	
	/**
	 * 生成表单实体，适用于POST/PUT
	 * @return 表单实体
	 */
	@Deprecated
	public UrlEncodedFormEntity buildFormEntity() {
		UrlEncodedFormEntity entity = null;
		
		try {
			entity = new UrlEncodedFormEntity(params, encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error(RequestConstants.MSG_UNSUPPORTED_ENCODING + e.toString());
		}
		
		return entity;
	}
	
	/**
	 * 生成请求实体
	 * @return
	 */
	public HttpEntity buildRequestEntity() {
		HttpEntity entity = null;
		
		//默认为URL类型请求
		if(requestEntityType == null) {
			requestEntityType = RequestEntityType.URL;
		}
		
		try {
			switch (requestEntityType) {
			case URL: {
				entity = new UrlEncodedFormEntity(params, Charset.forName(encoding));
				break;
			}
			case FILE: {
				entity = new FileEntity(requestFile, 
						contentType.withCharset(Charset.forName(encoding)));
				break;
			}
			case INPUT_STREAM: {
				entity = new InputStreamEntity(requestInputStream, 
						contentType.withCharset(Charset.forName(encoding)));
				break;
			}
			case BYTE_ARRAY: {
				entity = new ByteArrayEntity(requestByteArray, 
						contentType.withCharset(Charset.forName(encoding)));
				break;
			}
			case MULTIPART: {
				//大部分情况下使用浏览器兼容模式
				entity = this.multipartEntityBuilder.
						setCharset(Charset.forName(encoding)).build();
				
				break;
			}
			default:
				break;
			}
			
		} catch (Exception e) {
			logger.error(RequestConstants.MSG_UNSUPPORTED_ENCODING + e.toString());
		}
		
		return entity;
		
	}
	
	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}


	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the requestType
	 */
	public RequestType getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * @param requestEntityType the requestEntityType to set
	 */
	public void setRequestEntityType(RequestEntityType requestEntityType) {
		this.requestEntityType = requestEntityType;
		
		if(requestEntityType == RequestEntityType.MULTIPART) {
			this.multipartEntityBuilder = MultipartEntityBuilder.
					create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		}
	}

	/**
	 * @param requestInputStream the requestInputStream to set
	 */
	public void setRequestInputStream(InputStream requestInputStream) {
		this.requestInputStream = requestInputStream;
	}

	/**
	 * @param requestFile the requestFilen to set
	 */
	public void setRequestFilename(File requestFile) {
		this.requestFile = requestFile;
	}

	/**
	 * @param requestByteArray the requestByteArray to set
	 */
	public void setRequestByteArray(byte[] requestByteArray) {
		this.requestByteArray = requestByteArray;
	}

	/**
	 * @return the logger
	 */
	public static Log getLogger() {
		return logger;
	}

}
