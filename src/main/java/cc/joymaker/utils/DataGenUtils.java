package cc.joymaker.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;


/**
 *
 * @author baozhe
 * @version 2015年3月4日
 *
 */
public class DataGenUtils {

	public static synchronized String getUniqueOrderid() {
		String[] YM = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		Calendar cal = Calendar.getInstance();
		String year = YM[cal.get(Calendar.YEAR) % 23];
		String month = YM[(cal.get(Calendar.MONTH) + 1)];
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		day = (day.length() <= 1) ? ("0" + day) : day;
		String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		hour = (hour.length() <= 1) ? ("0" + hour) : hour;
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		minute = (minute.length() <= 1) ? ("0" + minute) : minute;
		String second = String.valueOf(cal.get(Calendar.SECOND));
		second = (second.length() <= 1) ? ("0" + second) : second;

		StringBuffer sb = new StringBuffer("TZ");
		String shortx = "";
		try {
			shortx = shortUrl("abcde", InetAddress.getLocalHost().getHostAddress()).toUpperCase();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		sb.append(shortx).append(year).append(month).append(day).append(hour).append(minute).append(second)
				.append(String.valueOf(Math.random() * 3).substring(2, 6));
		String uniqueNumber = sb.toString();
		return uniqueNumber;

	}

	public static synchronized String getUniqueUserid() {
		String uniqueStr = "";
		try {
			uniqueStr = DigestUtils
					.md5Hex(System.currentTimeMillis() + String.valueOf(Math.random() * 3).substring(2, 6))
					+ shortUrl("abcde", InetAddress.getLocalHost().getHostAddress()).toLowerCase();
		} catch (UnknownHostException e) {
			uniqueStr = DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())).toLowerCase();
		}
		return uniqueStr;
	}

	public static synchronized String getUniqueUUID() {
		String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		return uuid;
	}

	public static String getCharAndNumr(int length) {
		String val = "";

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				String thisChar = String.valueOf((char) (choice + random.nextInt(26)));
				if (StringUtils.equals(thisChar.toUpperCase(), "O") || StringUtils.equals(thisChar.toUpperCase(), "L")
						|| StringUtils.equals(thisChar.toUpperCase(), "I")) {
					i--;
					continue;
				}
				val += thisChar;

			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				int thisNum = random.nextInt(10);
				if (thisNum == 0) {
					i--;
					continue;
				}
				val += String.valueOf(thisNum);
			}
		}

		return val;
	}

	public static String getNumCode(int length) {
		String val = "";

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int sNum = random.nextInt(10);
			if (i == 0 && sNum == 0) {
				i--;
				continue;
			}
			val += sNum;
		}

		return val;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public synchronized static String getRandomString(String secret, int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		int x = 0;
		for (int i = 0; i < length - 1; i++) {
			int number = random.nextInt(base.length());

			if (i == 0 && base.charAt(number) == '0') {
				i--;
				continue;
			}
			sb.append(base.charAt(number));
			x += base.charAt(number);
		}

		sb.append(base.charAt((x + Math.abs(secret.hashCode())) % base.length()));
		return sb.toString();
	}

	public static String shortUrl(String key, String source) {
		// 可以自定义生成 MD5 加密字符传前的混合 KEY

		// 要使用生成 URL 的字符
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		// 对传入网址进行 MD5 加密
		String hex = DigestUtils.md5Hex(key + source);

		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {

			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);

			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 ,
			// 如果不用long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			for (int j = 0; j < 4; j++) {
				// 把得到的值与 0x0000001F 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000001F & lHexLong;
				// 把取得的字符相加
				outChars += chars[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 8;
			}
			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}
		Random random = new Random();
		int j = random.nextInt(4);// 产成4以内随机数
		return resUrl[j];
	}

	public static String shortCode10(String source) {
		// 可以自定义生成 MD5 加密字符传前的混合 KEY
		final String key = "%#HUYrtYj$UJE%^7";
		// 要使用生成 URL 的字符
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		// 对传入网址进行 MD5 加密
		String hex = DigestUtils.md5Hex(key + source);

		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {

			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);

			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 ,
			// 如果不用long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			for (int j = 0; j < 10; j++) {
				// 把得到的值与 0x0000001F 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000001F & lHexLong;
				// 把取得的字符相加
				outChars += chars[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 3;
			}
			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}
		Random random = new Random();
		int j = random.nextInt(4);// 产成4以内随机数
		return resUrl[j];
	}

	public static final String encryptPwd(String salt, String pwd) {
		return DigestUtils.md5Hex(salt + pwd);
	}

	public static void main(String[] args) throws InterruptedException, UnknownHostException {
		//System.out.println(getRandomString("abc", 10));
		String x = "TFhwjPeMl";
		int y = 0;
		for (int i = 0; i < x.length(); i++) {
			y += x.charAt(i);
		}
		System.out.println(y + "," + "abc".hashCode());
		int z = (y + Math.abs("abc".hashCode())) % 62;
		System.out.println(z);
		
		
		System.out.println(DataGenUtils.getRandomString("secret_2", 16));
		
		System.out.println(DataGenUtils.getNumCode(6));
	}
}
