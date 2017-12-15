package cc.joymaker.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.joymaker.weiop.open.base.utils.HmacSHA1;

public class AliSignature {

	private static final Logger logger = LoggerFactory.getLogger(AliSignature.class);

	public static String percentEncode(String value) throws UnsupportedEncodingException {
		return value != null
				? URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
	}

	public static String getSign(Map<String, Object> map) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result = result.substring(0, result.length() - 1);
		// result += "key=" + Configure.getKey();
		// logger.info("Sign Before MD5:" + result);
		// result = MD5.MD5Encode(result).toUpperCase();
		// logger.info("Sign Result:" + result);
		return result;
	}

	public static String getSign(Map<String, Object> map, String HTTPMethod) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue());
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort);
		// logger.info("AliSignature arrayToSort : {}",arrayToSort.toString());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			try {
				String[] s1 = arrayToSort[i].split("=");
				sb.append(percentEncode(s1[0])).append("=").append(percentEncode(s1[1])).append("&");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}

		}
		String result = sb.toString();
		result = result.substring(0, result.length() - 1);
		try {
			result = HTTPMethod + "&" + percentEncode("/") + "&" + percentEncode(result);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

		result = HmacSHA1.SHA1Encode(System.getProperty("ali.sms.accesssecret") + "&", result);
		// logger.info("Sign Result:" + result);
		return result;
	}

	public static String getSign(List<NameValuePair> params, String HTTPMethod) {
		ArrayList<String> list = new ArrayList<String>();
		for (NameValuePair p : params) {
			list.add(p.getName() + "=" + p.getValue());
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort);
		// logger.info("AliSignature arrayToSort : {}",arrayToSort.toString());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			try {
				String[] s1 = arrayToSort[i].split("=");
				sb.append(percentEncode(s1[0])).append("=").append(percentEncode(s1[1])).append("&");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}

		}
		String result = sb.toString();
		result = result.substring(0, result.length() - 1);
		try {
			result = HTTPMethod + "&" + percentEncode("/") + "&" + percentEncode(result);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

		result = HmacSHA1.SHA1Encode(System.getProperty("ali.sms.accesssecret") + "&", result);

		return result;
	}

}
