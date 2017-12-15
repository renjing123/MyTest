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




public class WeiopAPICoupon {

	private static final Logger log = LoggerFactory.getLogger(WeiopAPICoupon.class);
	
	public static void main(String[] args) throws Exception {
//		CreateCouponBatch();	//黄芪：cbh-1R1gzW，红花：cbh-Rw2Q9G
//		GenerateCoupons();
//		EnableCouponBatch();
//		BindCoupon();	//182电话的，2be7d4fc056168f2c912979591277133
//		SendCoupon();
//		VerifyCoupon();
//		GetUserCoupons();
		GetCoupon();
//		GetBatchCoupons();
//		GetMyVerifyLog();
//		GetShopVerifyLog();
//		BatchVerifyCoupon();
//		dispatchCoupon();
//		updateCoupon();
	}
	
	private static void updateCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("code", "9998575926100303");
		map.put("orderid", "CJJ4I2SH161229027720");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","UpdateCoupon",json);
		
	}

	private static void dispatchCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("activity_id", "TST_3To1_HQ_EX");
		map.put("uid", "064d82415c394906ead0d7ae4ef34241");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","DispatchCoupon",json);

	}

	private static void BatchVerifyCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("codes", "cbh-Xb6jIkcop-XUNqbg4BQB0JJptE,cbh-Xb6jIkcop-JRsVOI8b75cQeu2f");
		map.put("supplier_id", "小任任药房");
		map.put("uid", "bb");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","BatchVerifyCoupon",json);
		
	}

	public static void CreateCouponBatch() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("capacity", 1000);
		map.put("effective_time", 1496275200000L);	//2017-06-01
		map.put("expire_time", 1506816000000L);	//2017-10-01
		map.put("type", 2);	//1:代金券,2:实物兑换券
		map.put("exchangeType", 2);	//1:online,2:offline,兑换券时使用
		map.put("forward_flag", true);	//不可转增
		map.put("limit_per_order", 1);	//每个订单可以使用几个卡券
		map.put("limit_per_user", 1);	//每个用户限领几张，0不限制
		map.put("logo", "");	
		map.put("share_flag", false);	//不可裂变
		map.put("supplier_id", "honghuayaoye");
		map.put("title", "黄芪口服液一疗程兑换券测试");
//		map.put("title", "红花口服液20元代金额代金券测试");
		map.put("pin_length", 0);
		map.put("limitPrice", 0);	//代金券限制订单价格
		map.put("limitType", 1);
		map.put("product_id", "TST-HQKFY");
//		map.put("product_id", "TST-HHKFY");
		map.put("activity_id", "TST-3To1-HQ");
//		map.put("activity_id", "TST-ALLVERIFY-HQ");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","CreateCouponBatch",json);
	}
	
	
	public static void GenerateCoupons() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
//		map.put("batch_id", "cbh-Rw2Q9G");
//		map.put("title_prefix", "红花口服液");
		map.put("batch_id", "cbh-dzJ4fF");
		map.put("title_prefix", "黄芪口服液一疗程");
//		map.put("price", 200);
		map.put("withSerialNo", false);
		map.put("domain", "https://testcj.tbul.cn/cp/");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","GenerateCoupons",json);
	}
	
	
	public static void BindCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("code", "4797855352408780");
		map.put("uid", "1a12d29d9e06641934d26da13ded068b");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","BindCoupon",json);
	}
	
	
	public static void SendCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-dzJ4fF");
		map.put("code", "0555323565082560");
		map.put("uid", "064d82415c394906ead0d7ae4ef34241");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","SendCoupon",json);
	}
	
	
	
	public static void VerifyCoupon() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("code", "1033275102692076");
		map.put("supplier_id", "小任任药房");
		map.put("uid", "1a12d29d9e06641934d26da13ded068b");
		
		String json = JsonUtils.formBean(map);
		request("post", "coupon","VerifyCoupon",json);
	}
	
	
	public static void EnableCouponBatch() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-dzJ4fF");
		
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
		map.put("code", "4797855352408780");
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetCoupon",json);
	}
	
	
	public static void GetBatchCoupons() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("batch_id", "cbh-1R1gzW");
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetBatchCoupons",json);
	}
	
	
	public static void GetMyVerifyLog() throws Exception {
		Map<String, Object> map = new HashMap<>();
//		map.put("bindUid", "1234");
		map.put("uid", "2fc2cba94de1c3c6744bccca007ce7e3");
//		map.put("productId", "TST-HQKFY");
//		map.put("uid", "bb");
//		map.put("limit", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetMyVerifyLog",json);
	}
	
	
	public static void GetShopVerifyLog() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("shopName", "小任的店");
		map.put("productId", "TST-HHKFY");
//		map.put("limit", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "coupon","GetShopVerifyLog",json);
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
