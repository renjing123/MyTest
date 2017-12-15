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

public class WeiopAPIOrder {

	private static final Logger log = LoggerFactory.getLogger(WeiopAPIOrder.class);

	public static void main(String[] args) throws Exception {
//		 GetOrders();
		 GetOrder();
//		 GetOrderList();
//		 CreateOrder();
		// GetOrderAuditLog();
		// UpdateAddress();
		// GetOrdersByMobile();
//		 CreatePlan();
//		 JoinActivityToPlan();
//		 EnablePlan();
//		AddSupplierQuantity();
//		 AddItemToPlan();
//		 PayOrder();
//		GetOrdersByActivityId();
//		updateOrder();
//		GetActivityPlan();
//		GetAllProducts();
	}

	private static void GetAllProducts() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		String json = JsonUtils.formBean(map);
		request("get", "order", "GetAllProducts", json);
	}
	
	private static void GetActivityPlan() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("activity_id", "HHYYWL001");
		map.put("with_detail", 1);
		
		String json = JsonUtils.formBean(map);
		request("get", "order", "GetActivityPlan", json);
	}

	public static void updateOrder() throws Exception{
		Map<String, Object> map = new HashMap<>();
		
		map.put("orderid", "CJVWVUSH011310495923");
		
		String json = JsonUtils.formBean(map);
		request("post", "order", "UpdateOrder", json);
	}

	public static void GetOrder() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("orderid", "CJVWVUSH071753197747");

		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrder", json);
	}
	
	public static void GetOrdersByActivityId() throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("activity_id", "HHYYWL001");
//		map.put("pay_status", 1);
//		map.put("status", 0);
//		map.put("start_time", "1498343400000");
//		map.put("end_time", "1499439600000");
		map.put("order_type", 4);
		map.put("product_ids", "TST_3To1_HQ_ex");
		
		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrdersByActivityId", json);
	}

	public static void GetOrders() throws Exception {
		Map<String, Object> map = new HashMap<>();

//		map.put("openid", "oCL6gs2p5LuttzxgiIucVVYVTJwY");
		map.put("activity_id", "HHYYWL001");
		 map.put("uid", "3f5feaa9f7074343a45ff6ab2563f9e8");
		// map.put("activity_id", "act01");

		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrders", json);
	}

	public static void GetOrderList() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("uid", "064d82415c394906ead0d7ae4ef34241");
		map.put("activity_id", "HHYYWL001");
		map.put("orderType", 4);
//		map.put("status", 1);
//		map.put("starTime", "2017-04-01 01:24:46");
//		map.put("endTime", "2017-06-10 00:00:00");

		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrderList", json);
	}

	public static void UpdateAddress() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("orderid", "CJ3SDVSG220907318681");
		map.put("province", "450000");
		map.put("city", "450500");
		map.put("district", "450512");
		map.put("recipient", "小健");
		map.put("acc", 1);
		map.put("lat", 38.429659);
		map.put("lng", 112.719977);
		map.put("mobile", "18563496677");
		map.put("address", "凯旋城7号楼702");

		String json = JsonUtils.formBean(map);
		request("post", "order", "UpdateAddress", json);
	}

	public static void PayOrder() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("orderid", "CJKNMWSH011315219422");

		String json = JsonUtils.formBean(map);
		request("post", "order", "PayOrder", json);
	}

	public static void GetOrderAuditLog() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("orderid", "CJ3SDVSG220907318681");
		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrderAuditLog", json);
	}

	public static void GetOrdersByMobile() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("mobile", "18248116939");
		map.put("role", 1);

		String json = JsonUtils.formBean(map);
		request("get", "order", "GetOrdersByMobile", json);
	}

	public static void CreateOrder() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("activity_id", "HHYYWL001");
		map.put("type", 2); //订单类型：1、电商订单；2、红包订单；3、实物奖品订单；4、虚拟卡券订单
		map.put("uid", "064d82415c394906ead0d7ae4ef34241");
		map.put("source", 4); //来源，抽奖获得
		List details = new ArrayList<>();
		Map<String, Object> detail = new HashMap<>();
		detail.put("product_id", "TST-hhrpl-1.8");
		detail.put("quantity", 1);
//		detail.put("code", "9994017535323796");
		details.add(detail);
		map.put("details", details);

		String json = JsonUtils.formBean(map);
		request("post", "order", "CreateOrder", json);
	}

	public static void CreatePlan() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("begin_time", 1496246400000l); // 2017-06-01 00:00:00
		map.put("creator", "renjing");
		map.put("end_time", 1496246400000l); // 2017-10-01 00:00:00
		// map.put("extend", 1);
		map.put("near_line_action", 2); // 1 - 自动延期 2 - 关闭
		map.put("orgid", "cjmobile");

		String json = JsonUtils.formBean(map);
		request("post", "order", "CreatePlan", json);
	}

	public static void JoinActivityToPlan() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("plan_id", "pln-b3e5578b52138984c");
		map.put("activity_id", "HHYYWL001");

		String json = JsonUtils.formBean(map);
		request("post", "order", "JoinActivityToPlan", json);
	}

	public static void EnablePlan() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("plan_id", "pln-b3e5578b52138984c");

		String json = JsonUtils.formBean(map);
		request("post", "order", "EnablePlan", json);
	}

	public static void AddItemToPlan() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("plan_id", "pln-b3e5578b52138984c");
		map.put("price", 0);
		map.put("product_id", "TST-GIFTBOX-HQ");
		map.put("product_name", "黄芪口服液随机金额代金券测试");
//		map.put("product_id", "TST_3To1_HQ_ex");
//		map.put("product_name", "黄芪口服液1疗程兑换券测试");
		map.put("supplier_id", "cjmobile");
		map.put("plan_quantity", 1000);
		map.put("product_type", 2); // 虚拟

		String json = JsonUtils.formBean(map);
		request("post", "order", "AddItemToPlan", json);
	}

	public static void AddSupplierQuantity() throws Exception {
		Map<String, Object> map = new HashMap<>();

		map.put("product_id", "TST-GIFTBOX-HQ");
//		map.put("product_id", "TST_3To1_ex");
		map.put("product_name", "黄芪口服液随机金额代金券测试");
//		map.put("product_name", "黄芪口服液1疗程兑换券测试");
		map.put("price", 0);
		map.put("quantity", 1000);
		map.put("supplier_id", "cjmobile");

		String json = JsonUtils.formBean(map);
		request("post", "order", "AddSupplierQuantity", json);
	}

	public static void request(String method, String module, String api, String body) throws Exception {

		log.info("module:" + module + ",api:" + api + ",method:" + method + ",body:" + body);
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
				params += ("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), "UTF-8"));
			}
			if (StringUtils.isNotBlank(params) && params.startsWith("&")) {
				params = params.substring(1);
			}

			// 测试：
			// domain = test.open.wcsocial.com
			// appid = "ynzy-33i1";
			// appSecret = "jd8NJK0jixzM3"
			// 线上：
			// domain = open.yucent.com
			// appid = "ynzy-33i1";
			// appSecret = "grtH45uiERgyh"

			String url = "http://test.open.wcsocial.com/api/v1/" + module + "/" + api + "?" + params;
//			 String url = "https://open.yucent.com/api/v1/" + module + "/" +
//			 api + "?" + params;
			HttpGet request = new HttpGet(url);

//			//线上
//			String appid = "xingxingpeixun";
//			String appSecret = "2PDEjjLc4c94IiY2";
			//测试
//			String appid = "xingxingpeixun";
//			String appSecret = "MuDAmbvAJ74tIHcH";
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
//			 HttpPost request = new HttpPost("https://open.yucent.com/api/v1/"
//			 + module + "/" + api);

			//线上
//			String appid = "xingxingpeixun";
//			String appSecret = "2PDEjjLc4c94IiY2";
			//测试
//			String appid = "xingxingpeixun";
//			String appSecret = "MuDAmbvAJ74tIHcH";
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
