package liaowangClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class LiaoWangTest {

	public static String domain = "http://zhikuyun.lwinst.com/Liems/web/cms/";
//	public static String domain = "http://122.112.207.211:8080/Liems/web/cms/";
	public static String appid = "liaowang";
	public static String appSecret = "v9f6DwGfIuY1Q";

	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		getUserList(null, "LWZK", null, null);
		long end = System.currentTimeMillis();
//		addPoint("军事", "测试标题", "测试内容", "测试子标题", "测试作者", "测试职位", "测试来源", "测试摘要");

		System.out.println("程序运行时间： "+(end-start)+"ms");
		List<String> list = Arrays.asList("peter", "anna", "make");
		Collections.sort(list, (String a, String b) -> a.compareTo(b));

	}

	public static void getUserList(String role, String did, Integer page, Integer size) {

		String clientUrl = domain + "getUserList";
		long timestamp = System.currentTimeMillis();
		String sign = DigestUtils.md5Hex(appid + timestamp + appSecret);

		Map<String, String> map = new HashMap<>();
		map.put("appid", appid);
		map.put("appSecret", appSecret);
		map.put("timestamp", timestamp + "");
		map.put("sign", sign);
		map.put("did", did);
		map.put("role", role);
		if (page != null && size != null) {
			map.put("page", page + "");
			map.put("size", size + "");
		}

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpGet(clientUrl, Collections.EMPTY_MAP, map, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

	
	
	public static void addPoint(String column, String title, String content, String shortTitle, String author, String position,
			String resource, String sumary) {

		String clientUrl = domain + "addPoint";
		long timestamp = System.currentTimeMillis();
		String sign = DigestUtils.md5Hex(appid + timestamp + appSecret);

		Map<String, String> map = new LinkedHashMap<>();
		map.put("info", "jsonObject");
		map.put("column", column);
		map.put("title", title);
		map.put("content", content);
		map.put("shortTitle", shortTitle);
		map.put("author", author);
		map.put("position", position);
		map.put("resource", resource);
		map.put("sumary", sumary);
		map.put("createTime", new Date() + "");

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpGet(clientUrl, Collections.EMPTY_MAP, map, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

}
