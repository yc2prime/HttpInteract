HttpInteract
============

HttpInteract 是一个基于 Apache HttpClient 4.3 的客户端实现，提供上下文支持，支持当前主流开发框架下的四种Http请求类型以及多种数据实体类型，大幅简化了API。



特性
--------------------
HttpInteract的主要特点如下：

1. API简单一致，putParam方法可以放入String, byte[], InputStream, File，并且支持链式调用，支持分块发送请求。
2. 全局单例的管理器，支持与服务进行上下文会话，使用了连接池的实现方法，可定制的网络参数，实现了高效的内存使用。
3. 当前大部分服务器不支持PUT和DELETE的Http请求，HttpInteract进行了优化，可以直接发送PUT和DELETE请求。
4. 可与Spring框架整合，调用方便。


示例
--------------------

### 在Java语言里面直接使用请求管理器

	public static void main(String[] args) throws IllegalStateException, IOException {
		RequestObject obj = new RequestObject();
		obj.setRequestType(RequestType.PUT);
		obj.setRequestEntityType(RequestEntityType.MULTIPART);

		File file = new File("/Users/xxx/Desktop/a.png");

		obj.setScheme("http");
		obj.setHost("192.168.1.100");
		obj.setPath("/project/submit");
		obj.setPort(8080);
		obj.setContentType(ContentType.create("image/png"));
		obj.setEncoding(RequestConstants.ENCODING_UTF_8);
		obj.putParam("name", "jack").putParam("contact", "159512345678")
		.putParam("email", "jack@email.com").putParam("id", "73")
		.putParam("job", "coder").putParam("major", "mysql")
		.putParam("imgFile", file);
		
		
		ManagerConfig config = ManagerConfig.build("config");
		RequestManager.getInstance().config(config);
		CloseableHttpResponse response = RequestManager.getInstance().sendRequest(obj);

		System.out.println(obj.toUrl());
		System.out.println(RequestManager.parseResponse(response));
	}

### 与Spring框架整合 (applicationContext.xml)
	<?xml version="1.0" encoding="UTF-8"?>
	<beans
	    xmlns="http://www.springframework.org/schema/beans"
	    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	    xmlns:context="http://www.springframework.org/schema/context"
	    xsi:schemaLocation="
	     http://www.springframework.org/schema/beans
	     http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	     http://www.springframework.org/schema/context
	     http://www.springframework.org/schema/context/spring-context-3.1.xsd">
		
	    <context:property-placeholder location="classpath:config.properties" ignore-unresolvable="true"/>
	    
	    <!-- Config request -->
	    <bean id="requestConfig" class="com.zkhy.http.ManagerConfig">
	        <property name="connection" value="${connection}"/>
	        <property name="connectionTimout" value="${connection.timeout}"/>
	        <property name="preroute" value="${preroute}"/>
	        <property name="socketTimeout" value="${socket.timeout}"/>
	    </bean>
	    
	    <!-- Init request manager -->
	    <bean id="requestManager" class="com.zkhy.http.RequestManager" 
	         factory-method="getInstance" scope="singleton"/>
	         
	    <bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean" >
	        <property name="targetMethod" value="config"/>
	        <property name="targetObject" ref="requestManager"/>
	        <property name="arguments">
	            <list>
	                <ref bean="requestConfig"/>
	            </list>
	        </property>
	    </bean>
	</beans>

可以这样初始化请求管理器，并发送请求

	public static void main(String[] args) {
		ApplicationContext context = new 
				ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		RequestManager manager = context.getBean("requestConfig", RequestManager.class);
		RequestObject obj = new RequestObject();
		obj.setRequestType(RequestType.POST);

		obj.setScheme("http");
		obj.setHost("baidu.com");
		obj.setPort(8080);
		obj.setEncoding(RequestConstants.ENCODING_UTF_8);
		
		CloseableHttpResponse response = RequestManager.getInstance().sendRequest(obj);
		
		System.out.println(RequestManager.parseResponse(response));
	}

HttpInteract可能的用途
=================
1. 自动化黑盒测试框架
2. SOA架构下服务器间的通信
3. 网络爬虫
