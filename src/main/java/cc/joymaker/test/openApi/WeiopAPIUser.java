package cc.joymaker.test.openApi;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import cc.joymaker.utils.JsonUtils;




public class WeiopAPIUser {

	private static final Logger log = LoggerFactory.getLogger(WeiopAPIUser.class);
	
	public static void main(String[] args) throws Exception {
//		GetUserActTimes();
//		GetRetailerByMobile();
//		UpdateUserByWechat();
//		GetUserInventoryByCode();
//		GetUserByOpenid();
//		UpdateUserInfo();
//		getSpRetailerByUid();
		getUser();
	}
	
	
	private static void getSpRetailerByUid() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("uid", "2333b5f97091b69e2bc9db14e5fbdbc7");
		map.put("activityId", "HHYYWL001");
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetSpRetailerByUid",json);
	}


	private static void getUser() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("uid", "45e07bb342742288375969230c2c10d1");
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetUser",json);
	}


	private static void UpdateUserInfo() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("uid", "2be7d4fc056168f2c912979591277133");
		map.put("username", "任静");
		map.put("mobile", "18248116939");
		map.put("birth", "1993-05-04");
		map.put("address", "北京市昌平区");
		map.put("gender", 2);
		
		String json = JsonUtils.formBean(map);
		request("post", "user","UpdateUserInfo",json);
		
	}


	public static void GetUserByOpenid() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("openid", "oo38ovyeM5WHmIaFbQOv2T5DAUXA");
		map.put("role", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetUserByOpenid",json);
	}
	
	
	public static void GetUserActTimes() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("uid", "1234");
		map.put("activityId", "SXACT001");
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetUserActTimes",json);
	}
	
	
	public static void GetRetailerByMobile() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("mobile", "18500742947");
		map.put("orgid", "yunnanzhongyan");
		map.put("role", 2);
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetUserByMobile",json);
	}
	
	
	public static void UpdateUserByWechat() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("openid", "oo38ov6r7O6SCDrBDPOX_yvRwCu9");
		map.put("role", 2);
		map.put("nickname", "小任");
		
		String json = JsonUtils.formBean(map);
		request("post", "user","UpdateUserByWechat",json);
	}
	
	
	public static void GetUserInventoryByCode() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("code", "6T3mRETtD6TEu220");
		map.put("batchNo", "yn1001");
		
		String json = JsonUtils.formBean(map);
		request("get", "user","GetUserInventoryByCode",json);
	}
	
	

	public static void request(String method, String module, String api, String body) throws Exception {


		log.info("module:" + module + ",api:" + api + ",method:" + method + ",body:" +body);
		HttpClientBuilder builder = HttpClients.custom();
		builder.setRedirectStrategy(new RedirectStrategy() {

			@Override
			public boolean isRedirected(HttpRequest arg0, HttpResponse arg1, HttpContext arg2)
					throws ProtocolException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public HttpUriRequest getRedirect(HttpRequest arg0, HttpResponse arg1, HttpContext arg2)
					throws ProtocolException {
				// TODO Auto-generated method stub
				return null;
			}
		});

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub
				return true;
			}
		}).build();
		builder.setSslcontext(sslContext);

		CloseableHttpClient client = builder.build();

		if ("get".equalsIgnoreCase(method)) {

			Map<String, Object> mj = JsonUtils.toMap(body);
			String params = "";
			for (Entry<String, Object> e : mj.entrySet()) {
				params += ("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(),"UTF-8"));
			}
			if(StringUtils.isNotBlank(params) && params.startsWith("&")){
				params = params.substring(1);
			}

			//测试：
			//domain = test.open.wcsocial.com
			//appid = "ynzy-33i1";
			//appSecret = "jd8NJK0jixzM3"
			//线上：
			//domain = open.yucent.com
			//appid = "ynzy-33i1";
			//appSecret = "grtH45uiERgyh"
			
			
			String url = "http://test.open.wcsocial.com/api/v1/" + module + "/" + api + "?" + params;
//			String url = "https://open.yucent.com/api/v1/" + module + "/" + api + "?" + params;
			HttpGet request = new HttpGet(url);
			
			String appid = "cjmobile";
			String appSecret = "IzSNk7yEOQNnxHVg";
//			String appid = "testApp";
//			String appSecret = "XyWY8k3DLtGuXidb";
//			String appid = "ynzy-33i1";
//			String appSecret = "jd8NJK0jixzM3";
//			String appSecret = "grtH45uiERgyh";
			String timestamp = System.currentTimeMillis() + "";
			String nonce = UUID.randomUUID().toString();
			
			String base = module + api + appid + nonce + timestamp + appSecret;
			String sign = DigestUtils.md5Hex(base);

			request.setHeader("Content-Type", "application/json;charset=utf-8");

			request.addHeader("appid", appid);
			request.addHeader("timestamp", timestamp);
			request.addHeader("nonce", nonce);
			request.addHeader("sign", sign);

			request.addHeader("Client-Ip", "106.37.102.5");
			request.addHeader("Client-Acc", "1");
			request.addHeader("Device-Id", UUID.randomUUID().toString());

			HttpResponse resp = client.execute(request);
			System.out.println(EntityUtils.toString(resp.getEntity()));
		} else {

			HttpPost request = new HttpPost("http://test.open.wcsocial.com/api/v1/" + module + "/" + api);
//			HttpPost request = new HttpPost("https://open.yucent.com/api/v1/" + module + "/" + api);

			String appid = "cjmobile";
			String appSecret = "IzSNk7yEOQNnxHVg";
//			String appid = "ynzy-33i1";
//			String appSecret = "jd8NJK0jixzM3";
//			String appSecret = "grtH45uiERgyh";
			String timestamp = System.currentTimeMillis() + "";
			String nonce = UUID.randomUUID().toString();

			String base = module + api + appid + nonce + timestamp + appSecret;
			String sign = DigestUtils.md5Hex(base);

			request.setHeader("Content-Type", "application/json;charset=utf-8");

			request.addHeader("appid", appid);
			request.addHeader("timestamp", timestamp);
			request.addHeader("nonce", nonce);
			request.addHeader("sign", sign);

			request.addHeader("Client-Ip", "106.37.102.5");
			request.addHeader("Client-Lat", "39.904989");
			request.addHeader("Client-Lng", "116.405285");
			request.addHeader("Client-Acc", "1");
			request.addHeader("Device-Id", UUID.randomUUID().toString());

			request.setEntity(new StringEntity(body, "UTF-8"));

			HttpResponse resp = client.execute(request);
			System.out.println(EntityUtils.toString(resp.getEntity()));
		}
	}
}
