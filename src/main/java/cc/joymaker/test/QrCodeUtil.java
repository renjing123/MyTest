package cc.joymaker.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 生成二维码
 * 
 * @author zhangyuxin85@gmail.com
 *
 */
public class QrCodeUtil {

	public static final int WIDTH = 300;
	public static final int HEIGHT = 300;
	public static final String FORMAT = "png";

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	synchronized public static final void encode(String str, OutputStream stream) throws Exception {
		writeToStream(getBitMatrix(str), FORMAT, stream);
	}

	public static final void encode(String str, File file) throws Exception {
		writeToFile(getBitMatrix(str), FORMAT, file);
	}

	public static final BufferedImage encode(String str) throws Exception {
		return toBufferedImage(getBitMatrix(str));
	}

	private static BitMatrix getBitMatrix(String content) throws WriterException {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);// 生成矩阵
		return bitMatrix;
	}

	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}

	private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		}
	}

	private static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

//	private static void writeToFile(BitMatrix matrix, String format, File file, BufferedImage logo, BufferedImage bg, String text) throws IOException {
//		// 二维码
//		BufferedImage image = toBufferedImage(matrix);
//		QRLogoConfig QRLogoConfig = new QRLogoConfig(logo, bg, text);
//		Map<String, Object> logoConfig = new HashMap<>();
//		image = QRLogoConfig.LogoMatrix(image, logoConfig);
//
//		if (!ImageIO.write(image, format, file)) {
//			throw new IOException("Could not write an image of format " + format + " to " + file);
//		} else {
//			System.out.println("图片生成成功！");
//		}
//	}

	/**
	 * 二维码海报
	 * 
	 * @param qrStr
	 *            二维码内容
	 * @param file
	 *            输出文件
	 * @param iconImgUrl
	 *            头像url
	 * @param bannerImgUrl
	 *            商图品url
	 * @param bgImgUrl
	 *            背景图url
	 * @param index
	 *            海报序号
	 * @throws Exception
	 */
//	public static void encodePoster(String qrStr, File file, String iconImgUrl, String bannerImgUrl, String bgImgUrl, int index) throws Exception {
//		String pattern = "No.%d";
//		String text = String.format(pattern, index);
//		posterToFile(qrStr, FORMAT, file, iconImgUrl, bannerImgUrl, bgImgUrl, text);
//	}

	/*
	 * 输出到文件
	 * 
	 * @param qrUrl 二维码 url
	 * 
	 * @param format 输出格式
	 * 
	 * @param file 输出文件
	 * 
	 * @param iconImgUrl 头像url
	 * 
	 * @param bannerImgUrl banner图url
	 * 
	 * @param bgImgUrl 背景地图url
	 * 
	 * @param text 中间显示的活动文本
	 * 
	 * @throws IOException
	 */
//	private static void posterToFile(String qrUrl, String format, File file, String iconImgUrl, String bannerImgUrl, String bgImgUrl, String text)
//			throws Exception {
//
//		// 头像
//		// URL iconUrl = new URL(iconImgUrl);
//		InputStream iconImgStream = null;
//		InputStream bannerStream = null;
//		InputStream bgImgStream = null;
//		InputStream qrcodeStream = null;
//
//		try {
//			iconImgStream = request(iconImgUrl);
//			BufferedImage icon = ImageIO.read(iconImgStream);
//			// 中间banner大图
//			// URL bannerUrl = new URL(bannerImgUrl);
//			bannerStream = request(bannerImgUrl);
//			BufferedImage banner = ImageIO.read(bannerStream);
//			// 背景图
//			// URL bgUrl = new URL(bgImgUrl);
//			bgImgStream = request(bgImgUrl);
//			BufferedImage bgImg = ImageIO.read(bgImgStream);
//
//			// 二维码
//			qrcodeStream = request(qrUrl);
//			BufferedImage qr = ImageIO.read(qrcodeStream);// toBufferedImage(getBitMatrix(qrUrl));
//
//			QRLogoConfig QRLogoConfig = new QRLogoConfig(bgImg, icon, banner, qr, text);
//			Map<String, Object> logoConfig = new HashMap<>();
//			BufferedImage poster = QRLogoConfig.PosterImageMatrix(logoConfig);
//			if (!ImageIO.write(poster, format, file)) {
//				throw new IOException("Could not write an image of format " + format + " to " + file);
//			} else {
//				System.out.println("图片生成成功！");
//			}
//		} catch (Exception ex) {
//			throw ex;
//		} finally {
//			if (iconImgStream != null) {
//				iconImgStream.close();
//			}
//			if (bannerStream != null) {
//				bannerStream.close();
//			}
//			if (bgImgStream != null) {
//				bgImgStream.close();
//			}
//			if (qrcodeStream != null) {
//				qrcodeStream.close();
//			}
//		}
//	}

	protected static InputStream request(String url) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		String proxyHost = System.getProperty("http.proxy.host");
		Integer proxyPort = Integer.getInteger("http.proxy.port");
		HttpClientBuilder builder = HttpClients.custom();
		if (proxyHost != null && proxyPort > 0) {
			builder.setProxy(new HttpHost(proxyHost, proxyPort));
		}

		if (url.startsWith("https")) {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();
			builder.setSslcontext(sslContext);
		}
		CloseableHttpClient client = builder.build();

		HttpUriRequest req = null;
		req = new HttpGet(url.toString());

		try {
			HttpResponse resp = client.execute(req);
			int code = resp.getStatusLine().getStatusCode();
			if (code == 200) {
				return resp.getEntity().getContent();
			}
		} finally {
			// client.close();
		}

		return null;

	}

	// @Deprecated
	// public static void posterToFile(File file, String format, String
	// avatarImgUrl, String qrImgUrl, String productImgUrl, String bgImgUrl,
	// String text)
	// throws IOException {
	// // 头像
	// URL avatarUrl = new URL(avatarImgUrl);
	// BufferedImage avatar = ImageIO.read(avatarUrl);
	// // 二维码
	// URL qrUrl = new URL(qrImgUrl);
	// BufferedImage qr = ImageIO.read(qrUrl);
	// // 中间banner大图
	// URL productUrl = new URL(productImgUrl);
	// BufferedImage product = ImageIO.read(productUrl);
	// // 背景图
	// URL bgUrl = new URL(bgImgUrl);
	// BufferedImage bgImg = ImageIO.read(bgUrl);
	//
	// QRLogoConfig QRLogoConfig = new QRLogoConfig(bgImg, avatar, product, qr,
	// text);
	// Map<String, Object> logoConfig = new HashMap<>();
	// BufferedImage image = QRLogoConfig.PosterImageMatrix(logoConfig);
	// if (!ImageIO.write(image, format, file)) {
	// throw new IOException("Could not write an image of format " + format + "
	// to " + file);
	// } else {
	// System.out.println("图片生成成功！");
	// }
	//
	// }

	public static void main(String[] args) throws Exception {
//		String iconImgUrl = "http://wx.qlogo.cn/mmopen/PiajxSqBRaEJra3iazdFGQSrnic6lOegaC1ZMyjIOu10E9a0j7tE3cOm48mBWHWAicMQTtQzarKwLQgibPHBnsm5yug/0";
//		InputStream is = request(iconImgUrl);
//		BufferedImage icon = ImageIO.read(is);
//		System.out.println(is.available() + "," + icon);
		String str = "https://xxpx.tbul.cn/?c=3Ifs8GPHiwEEC5k21mypdoU1bX";
		File file = new File("/Users/mac/Desktop/ss.jpg");
		QrCodeUtil.encode(str, file);
	}
}
