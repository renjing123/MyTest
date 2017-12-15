package cc.joymaker.taiheClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import cc.joymaker.utils.JsonUtils;





public class ScanTaiheTest {

	 public static String domain =
	 "http://yz2-f-stg.taiheiot.com/cloud2.extser";
	 public static String sysCode = "QXSJ";
	 public static String salt = "hskfsaf==";
	 public static String productCode = "qxs";
	 public static String barcode = "agfjwZyVYS6GM256";
	 public static String vcode = "700369";

//	public static String domain = "http://ynzy.yunzhi.co/yunzhi_verification";
//	public static String sysCode = "LYKJYXY";
//	public static String salt = "erlqkjwjrqo";
//	public static String productCode = "lykjyxy";
//	public static String barcode = "QDxoCryeEht3F287";
//	public static String vcode = "120058";

	public static void main(String[] args) {
		testGetInfoByCode();
		// getProducts();
//		 testVerify();

		List<String> list = Arrays.asList("peter", "anna", "make");
		Collections.sort(list, (String a, String b) -> a.compareTo(b));

	}

	public static void testGetInfoByCode() {
		Map<String, String> body = new HashMap<>();
		body.put("barcode", barcode);
		String json = JsonUtils.formBean(body);

		System.out.println("json:" + json);

		Map<String, String> map = new HashMap<>();
		long timestamp = System.currentTimeMillis();
		String key = DigestUtils.md5Hex(sysCode + timestamp + salt);

		map.put("sysCode", sysCode);
		map.put("timer", timestamp + "");
		map.put("key", key);
		map.put("serviceFlg", "queryCodeInfoByBarCode");
		map.put("params", json);

		map.putAll(body);

		String str = "";
		for (Entry<String, String> e : map.entrySet()) {
			try {
				str += ("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		if (StringUtils.isNotBlank(str) && str.startsWith("&")) {
			str = str.substring(1);
		}
		String url = domain + "/entry/service.do";

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpPost(url, Collections.emptyMap(), map, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

	public static void getProducts() {
		Map<String, String> body = new HashMap<>();
		String json = JsonUtils.formBean(body);

		System.out.println("json:" + json);

		Map<String, String> map = new HashMap<>();
		long timestamp = System.currentTimeMillis();
		String key = DigestUtils.md5Hex(sysCode + timestamp + salt);

		map.put("sysCode", sysCode);
		map.put("timer", timestamp + "");
		map.put("key", key);
		map.put("serviceFlg", "queryAllOrderMaterials");
		map.put("params", json);

		map.putAll(body);

		String str = "";
		for (Entry<String, String> e : map.entrySet()) {
			try {
				str += ("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		if (StringUtils.isNotBlank(str) && str.startsWith("&")) {
			str = str.substring(1);
		}
		String url = domain + "/entry/service.do";

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpPost(url, Collections.emptyMap(), map, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

	public static void testVerify() {

		Map<String, String> map = new HashMap<>();
		long timestamp = System.currentTimeMillis();
		String key = DigestUtils.md5Hex(barcode + productCode + salt);

		map.put("sysCode", sysCode);
		map.put("timer", timestamp + "");
		map.put("key", key);

		map.put("vcode", vcode);
		map.put("productCode", productCode);
		map.put("barcode", barcode);

		String str = "";
		for (Entry<String, String> e : map.entrySet()) {
			try {
				str += ("&" + e.getKey() + "=" + URLEncoder.encode(e.getValue().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		if (StringUtils.isNotBlank(str) && str.startsWith("&")) {
			str = str.substring(1);
		}

		String url = domain + "/verification/barcode.do";

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpPost(url, Collections.emptyMap(), map, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

}
