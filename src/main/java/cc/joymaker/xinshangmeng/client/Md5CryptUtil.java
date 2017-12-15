package cc.joymaker.xinshangmeng.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 加密工具类
 * MD5加密
 */
public class Md5CryptUtil {
	private static DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
    private static final String KEY_MD5 = "MD5";
    // 全局数组
    private static final String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    /**
     * MD5加密
     * @param strObj
     * @return
     * @throws Exception
     */
    public static String encrypt(String strObj) throws Exception{
        java.security.MessageDigest md = java.security.MessageDigest.getInstance(KEY_MD5);
        // md.digest() 该函数返回值为存放哈希值结果的byte数组
        return byteToString(md.digest(strObj.getBytes()));
    }

    
       public static String getAccessToken() throws Exception {
		//应用密钥：
    	  String appKey="856893e8211a66bcfe35e25df7a76829";
    	//当前时间：
    	  String currTime=dateFormat.format(new Date());
    	  String req=appKey+currTime;
    	  String res=encrypt(req);
    	  System.out.println("md5:"+res);
    	  String accessToken=res.substring(8,24);
          return accessToken;
       }
}

