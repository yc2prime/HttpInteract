HttpInteract
============

A http client providing context with simple API based on HttpClient 4.3


public class Test {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
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
	}
	
	
}