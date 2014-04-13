HttpInteract
============

A http client providing context with simple API based on HttpClient 4.3

Introducton
-----------
Request manager is a single instance object that manager all the requests and responses.

A Java Example
------------
	
public static void main(String[] args) throws IllegalStateException, IOException {
	RequestObject obj = new RequestObject();
	obj.setRequestType(RequestType.POST);

	obj.setScheme("http");
	obj.setHost("baidu.com");
	obj.setPort(8080);
	obj.setEncoding(RequestConstants.ENCODING_UTF_8);
	
	ManagerConfig config = ManagerConfig.build("config");
	RequestManager.getInstance().config(config);
	CloseableHttpResponse response = RequestManager.getInstance().sendRequest(obj);
	
	System.out.println(RequestManager.parseResponse(response));
}

Integration with Spring (applicationContext.xml)
-----------------------
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

And init and use the request manager like this:


public static void main(String[] args) {
	// TODO Auto-generated method stub
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
