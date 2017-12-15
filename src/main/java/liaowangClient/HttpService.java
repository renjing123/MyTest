
package liaowangClient;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author pautcher
 * @date 2014-12-31
 * 
 */
public interface HttpService {

	String PROXY_HOST = "proxy.hmtx.cc";
	int PROXY_PORT = 3128;

	final class HttpResult {
		private int code;
		private String result;

		public HttpResult(int code, String result) {
			this.code = code;
			this.result = result;
		}

		public int getCode() {
			return code;
		}

		public String getResult() {
			return result;
		}
	}

	final class HttpStreamResult {
		private int code;
		private InputStream is;

		public HttpStreamResult(int code, InputStream is) {
			this.code = code;
			this.is = is;
		}

		public int getCode() {
			return code;
		}

		public InputStream getIs() {
			return is;
		}
	}

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpGet(String url, Map<String, String> headers, Map<String, String> params, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpGet(String url, Map<String, String> headers, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param os
	 * @param timeout
	 * @return
	 */
	HttpStreamResult syncHttpStreamGet(String url, Map<String, String> headers, Map<String, String> params, OutputStream os, int timeout);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param body
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> body, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param file
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, File file, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpDelete(String url, Map<String, String> headers, Map<String, String> params, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param timeoutInMs
	 * @return
	 */
	HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params, String body, int timeoutInMs);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpGet(String url, Map<String, String> headers, Map<String, String> params, int timeoutInMs, boolean httpsFlag);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeoutInMs, boolean httpsFlag);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeoutInMs, boolean httpsFlag);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpDelete(String url, Map<String, String> headers, Map<String, String> params, int timeoutInMs, boolean httpsFlag);

	/**
	 * 
	 * @param url
	 * @param header
	 * @param params
	 * @param body
	 * @param content
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpPut(String url, Map<String, String> header, Map<String, String> params, Map<String, String> body, File content, int timeoutInMs,
			boolean httpsFlag);

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param body
	 * @param content
	 * @param timeoutInMs
	 * @param httpsFlag
	 * @return
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, File content, int timeoutInMs,
			boolean httpsFlag);

	HttpResult syncHttpPost(String url, Map<String, String> header, Map<String, Object> body, int timeoutInMs, String mimeType);

	/**
	 * @param url
	 * @param headers
	 * @param jsonStr
	 * @param timeoutInMs
	 */
	HttpResult syncHttpPost(String url, Map<String, String> headers, String body, int timeoutInMs);
}
