package cc.joymaker.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>
 * Title: HmacSHA1算法
 * </p>
 *
 *
 */
public final class HmacSHA1 {

	private static final String ALGORITHM = "HmacSHA1";
	private static final String ENCODING = "UTF-8";

	public static String SHA1Encode(String key, String stringToSign) {
		if (key == null || stringToSign == null) {
			return null;
		}
		try {
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM));
			byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));
			return new String(Base64.encodeBase64(signData));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}