package cc.joymaker.openApi;


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




public class WeiopAPICoupon {

	private static final Logger log = LoggerFactory.getLogger(WeiopAPICoupon.class);
	
	public static void main(String[] args) throws Exception {
//		CreateCouponBatch();	
		GenerateCoupons();
//		EnableCouponBatch();
//		BindCoupon();
//		SendCoupon();
//		VerifyCoupon();
//		GetUserCoupons();
//		GetCoupon();
//		GetBatchCoupons();
//		GetMyVerifyLog();
//		GetShopVerifyLog();
	}
	
	public static void CreateCouponBatch() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("capacity", 20000);
		map.put("effective_time", 1500480000000L);	//2017-07-20
		map.put("expire_time", 1509408000000L);	//2017-10-31
		map.put("type", 2);	//1:代金券,2:实物兑换券
		map.put("exchangeType", 2);	//1:online,2:offline,兑换券时使用
		map.put("forward_flag", true);	//不可转增
		map.put("limit_per_order", 1);	//每个订单可以使用几个卡券
		map.put("limit_per_user", 0);	//每个用户限领几张，0不限制
		map.put("logo", "");	
		map.put("share_flag", false);	//不可裂变
		map.put("supplier_id", "honghuayaoye");
		map.put("title", "黄芪口服液一疗程兑换券");
		map.put("pin_length", 0);
		map.put("limitPrice", 0);	//代金券限制订单价格
		map.put("limitType", 1);
//		map.put("product_id", "HQKFY");
		map.put("product_id", "HQKFY");
		map.put("activity_id", "3To1_HQ_EX");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","CreateCouponBatch",json);
	}
	
	
	public static void GenerateCoupons() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-B6A01X");
//		map.put("title_prefix", "黄芪口服液单盒");
		map.put("title_prefix", "黄芪口服液一疗程");
		map.put("withSerialNo", false);
//		map.put("price", 20);
		map.put("domain", "https://hhyy.cjiot.cc/cp");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","GenerateCoupons",json);
	}
	
	
	public static void BindCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		map.put("code", "cop-DC1mWaoHhQM0esIT");
		map.put("uid", "1234");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","BindCoupon",json);
	}
	
	
	public static void SendCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		map.put("code", "cop-DC1mWaoHhQM0esIT");
		map.put("uid", "aa");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","SendCoupon",json);
	}
	
	
	
	public static void VerifyCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		map.put("code", "cop-DC1mWaoHhQM0esIT");
		map.put("supplier_id", "小任任药房");
		map.put("uid", "bb");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","VerifyCoupon",json);
	}
	
	
	public static void EnableCouponBatch() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","EnableCouponBatch",json);
	}
	
	
	public static void GetUserCoupons() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("uid", "4444c15246d3cc3a9ceae1a034e87f6a");
//		map.put("uid", "aa");
//		map.put("status", 2);
//		map.put("limit", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetUserCoupons",json);
	}
	
	
	public static void GetCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		map.put("code", "cop-DC1mWaoHhQM0esIT");
		
		String json = JsonUtils.formBean(map);
//		System.out.println(""+json);
		request("get", "coupon","GetCoupon",json);
	}
	
	
	public static void GetBatchCoupons() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-IRhoy6suZnI6lG4W");
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetBatchCoupons",json);
	}
	
	
	public static void GetMyVerifyLog() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("bindUid", "1234");
//		map.put("uid", "4444c15246d3cc3a9ceae1a034e87f6a");
//		map.put("uid", "bb");
//		map.put("limit", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetMyVerifyLog",json);
	}
	
	
	public static void GetShopVerifyLog() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("shopName", "小任任药房");
//		map.put("limit", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetShopVerifyLog",json);
	}
	
	

	public static void request(String method, String module, String api, String body) throws Exception {
		System.out.println("json:"+body);

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
			
			
//			String url = "http://test.open.wcsocial.com/api/v1/" + module + "/" + api + "?" + params;
			String url = "https://open.yucent.com/api/v1/" + module + "/" + api + "?" + params;
			HttpGet request = new HttpGet(url);
			
			String appid = "honghuayaoye";
			String appSecret = "QA28tOs9sFaHRXRk";
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

//			HttpPost request = new HttpPost("http://test.open.wcsocial.com/api/v1/" + module + "/" + api);
			HttpPost request = new HttpPost("https://open.yucent.com/api/v1/" + module + "/" + api);

			String appid = "honghuayaoye";
			String appSecret = "QA28tOs9sFaHRXRk";
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
