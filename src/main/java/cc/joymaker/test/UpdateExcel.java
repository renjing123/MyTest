package cc.joymaker.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.digest.DigestUtils;
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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aliyun.ots.thirdparty.org.apache.client.utils.DateUtils;

import cc.joymaker.taiheClient.HttpService.HttpResult;
import cc.joymaker.utils.JsonUtils;
import cc.joymaker.taiheClient.HttpServiceImpl;


public class UpdateExcel {

	public static void main(String[] args) throws Exception {
		ExecutorService ex = Executors.newFixedThreadPool(1);
		ex.submit(new Runnable() {

			@Override
			public void run() {
				try {
					read();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private static void read() throws Exception {
		InputStream is = new FileInputStream("/Users/mac/Desktop/YqEfJ4.xlsx");
		XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}
			System.out.println("该文件共有：" + hssfSheet.getLastRowNum() + "行数据");
			String res = "", res2 = "";
			List<Map<String, String>> list = new ArrayList<>();
			// 循环行Row
			for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}

				String index = getValue(hssfRow.getCell(0));
				String url = getValue(hssfRow.getCell(1));
				String pattern = "%08d";
		        String text = String.format(pattern,Integer.parseInt(index));

				Map<String, String> m = new HashMap<String, String>();
				m.put("index", text);
				m.put("url", url);

				list.add(m);
			}
			testWrite(list, hssfSheet.getLastRowNum());
		}
		hssfWorkbook.close();
	}

	private static String getValue(XSSFCell hssfCell) {
		if (hssfCell == null) {
			return "";
		}
		if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			HSSFDataFormatter dataFormatter = new HSSFDataFormatter();

			String cellFormatted = dataFormatter.formatCellValue(hssfCell);
			return cellFormatted;
		} else {
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}
	
	
	 
	 
	 private static void testWrite(List<Map<String, String>> list, int cloumnCount) {
		 // 第一步，创建一个webbook，对应一个Excel文件  
	        HSSFWorkbook wb = new HSSFWorkbook();  
	        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
	        HSSFSheet sheet = wb.createSheet("sheet1");  
	        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
	        HSSFRow row = sheet.createRow((int) 0);  
	        // 第四步，创建单元格，并设置值表头 设置表头居中  
	        HSSFCellStyle style = wb.createCellStyle();  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
	  
	        HSSFCell cell = row.createCell((short) 0);  
	        cell.setCellValue("index");  
	        cell.setCellStyle(style);  
	        cell = row.createCell((short) 1);  
	        cell.setCellValue("url");  
	        cell.setCellStyle(style);  
	  
	        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，  
	        
	        for (int j = 0; j < list.size(); j++) {  
                // 创建一行：从第二行开始，跳过属性列  
                row = sheet.createRow(j + 1);  
                // 得到要插入的每一条记录  
                Map<String, String> dataMap = list.get(j);  
                String index = dataMap.get("index").toString();  
                String url = dataMap.get("url").toString();  
                for (int k = 0; k <= cloumnCount; k++) {  
                // 在一行内循环  
                Cell first = row.createCell(0);  
                first.setCellValue(index);  
          
                Cell second = row.createCell(1);  
                second.setCellValue(url);  
                }  
            }
	  
	        // 第六步，将文件存到指定位置  
	        try  
	        {  
	            FileOutputStream fout = new FileOutputStream("/Users/mac/Desktop/YqEfJ4.xlsx");  
	            wb.write(fout);  
	            fout.close();  
	        }  
	        catch (Exception e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }
	 
	 
	 

	private static String request(String module, String api, String method, String json) throws Exception {

		// File file = new
		// File("/Users/pautcherzhang/workspace/TestCase/weiop-qr/" + api +
		// ".json");
		// String json = FileUtils.readFileToString(file, "utf-8");

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

		String appid = "yunnanzhongyan";
		String appSecret = "ykjibOFI8MoSKQdY";
		String timestamp = System.currentTimeMillis() + "";
		String nonce = UUID.randomUUID().toString();

		String base = module + api + appid + nonce + timestamp + appSecret;
		String sign = DigestUtils.md5Hex(base);

		HttpUriRequest request = null;
		String domain = "https://open.yucent.com/api/v1";
		if ("get".equalsIgnoreCase(method)) {

			Map<String, Object> mj = JsonUtils.toMap(json);
			String params = "";
			for (Entry<String, Object> e : mj.entrySet()) {
				params += ("&" + e.getKey() + "=" + e.getValue().toString());
			}

			HttpGet get = new HttpGet(domain + "/" + module + "/" + api + "?" + params);
			request = get;
		} else {

			HttpPost post = new HttpPost(domain + "/" + module + "/" + api);
			post.setEntity(new StringEntity(json, "UTF-8"));

			request = post;
		}

		request.setHeader("Content-Type", "application/json;charset=utf-8");

		request.addHeader("appid", appid);
		request.addHeader("timestamp", timestamp);
		request.addHeader("nonce", nonce);
		request.addHeader("sign", sign);

		request.addHeader("Client-Ip", "106.120.8.58");
		// request.addHeader("Client-Lat", "38.429659");
		// request.addHeader("Client-Lng", "112.719977");
		request.addHeader("client-acc", "1");
		request.addHeader("device-id", UUID.randomUUID().toString());

		HttpResponse resp = client.execute(request);
		String x = EntityUtils.toString(resp.getEntity());
		return x;
	}

	public static void sendVoucher(String openid) throws UnsupportedEncodingException {

		String yz_secret = "128ce0054b9559217021e38422a25d79";

		String app_id = "a21162f1f812cc10ff";
		String coupon_group_id = "1724931";// "1719952";//40券
		String format = "json";
		String method = "kdt.ump.coupon.take";
		String sign_method = "md5";
		// var timestamp = "2013-05-06 13:52:03";
		Date now = new Date();
		String timestamp = DateUtils.formatDate(now, "yyyy-MM-dd HH:mm:ss");
		String v = "1.0";
		String weixin_openid = openid;

		String base = "app_id" + app_id + "coupon_group_id" + coupon_group_id + "format" + format + "method" + method
				+ "sign_method" + sign_method + "timestamp" + timestamp + "v" + v + "weixin_openid" + weixin_openid;
		base = yz_secret + base + yz_secret;

		String sign = DigestUtils.md5Hex(base);
		String url = "https://open.koudaitong.com/api/entry?sign=" + sign + "&timestamp="
				+ URLEncoder.encode(timestamp, "utf-8") + "&v=1.0&app_id=" + app_id + "&method=" + method
				+ "&sign_method=md5&format=json&coupon_group_id=" + coupon_group_id + "&weixin_openid=" + weixin_openid;

		HttpServiceImpl service = new HttpServiceImpl();
		HashMap<String, String> x = new HashMap<String, String>();
		HttpResult res = service.syncHttpPost(url, x, x, 5000);
		System.out.println(res.getResult());

	}
}
