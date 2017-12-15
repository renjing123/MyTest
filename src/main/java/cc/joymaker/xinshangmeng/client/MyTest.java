package cc.joymaker.xinshangmeng.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cc.joymaker.weiop.open.base.utils.JsonUtils;

public class MyTest {

	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String url = "http://testsc.xinshangmeng.com/scweb/qrcode/wechat/registRetailerMemberSync";
		map.put("appId", "11001");
		map.put("accessToken", Md5CryptUtil.getAccessToken());
		map.put("inJson",
				"[\"abc123129de12312\",\"18615230930\",\"abc123129de12312abc123129de12312\",\"370112100333\",\"李星堂\",\"历城区行唐商店\",\"山东省\",\"济南市\",\"历城区彩石乡宏图路1号\"]");
		String param = JsonUtils.formBean(map);

		HttpService service = new HttpServiceImpl();
		HttpService.HttpResult res = null;
		try {
			res = service.syncHttpPost(url, Collections.EMPTY_MAP, param, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Md5CryptUtil.getAccessToken());
		System.out.println("res code |" + res.getCode());
		System.out.println("res content |" + res.getResult());
	}

}
