package cc.joymaker.openApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchResponse;

public class HttpCilent {

	public static void main(String[] args) {
//		String url = "http://intra-es.yucent.com/users/retl/_search?q=c7e1da02f270222bcefe91e7b5c4daeb";
//
//		HttpService service = new HttpServiceImpl();
//		HttpService.HttpResult res = null;
//		try {
//			res = service.syncHttpGet(url, null, null, 500, false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("res code |" + res.getCode());
//		System.out.println("res content |" + res.getResult());
		
		
		HttpClient httpClient = new DefaultHttpClient();

	    HttpGet httpGet = new HttpGet("http://intra-es.yucent.com/users/retl/_search?q=c7e1da02f270222bcefe91e7b5c4daeb");
	    
	    HttpResponse response = null;  
	    try{
	        response = httpClient.execute(httpGet);
	    }catch (Exception e) {} 

	    String temp="";
	    try{
	        HttpEntity entity = response.getEntity();
	        temp=EntityUtils.toString(entity,"UTF-8");
	    }catch (Exception e) {} 
	    
	    System.out.println(temp);
	    
	}
}
