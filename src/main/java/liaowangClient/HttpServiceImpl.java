package liaowangClient;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.util.JSONUtils;

/**
 * @author pautcher
 * @date 2015-1-22
 * 
 */
public class HttpServiceImpl implements HttpService {

	Logger log = LoggerFactory.getLogger(HttpServiceImpl.class);

	private ExecutorService threadPool;

	private CloseableHttpClient getHttpClient() {
		CloseableHttpClient client;
		if (StringUtils.isBlank(System.getProperty("http.proxy.host"))) {
			client = HttpClients.custom().setMaxConnTotal(256).build();
		} else {
			HttpHost proxy = new HttpHost(System.getProperty("http.proxy.host"), Integer.getInteger("http.proxy.port"));
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

			client = HttpClients.custom().setMaxConnTotal(256).setRoutePlanner(routePlanner).build();
		}
		return client;
	}

	private CloseableHttpClient getHttpsClient() {
		try {

			TrustManager[] trustManagers = new TrustManager[1];
			trustManagers[0] = new DefaultTrustManager();

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustManagers, new SecureRandom());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			CloseableHttpClient httpclient = null;
			if (StringUtils.isBlank(System.getProperty("http.proxy.host"))) {
				httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setMaxConnTotal(256).build();
			} else {
				HttpHost proxy = new HttpHost(System.getProperty("http.proxy.host"), Integer.getInteger("http.proxy.port"));
				DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

				httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setMaxConnTotal(256).setRoutePlanner(routePlanner).build();
			}

			return httpclient;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {

		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}
	}

	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> body, int timeoutInMs) {
		Throwable cause = null;
		try {
			return makeFormPost(url, headers, body, timeoutInMs);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeout) {
		Throwable cause = null;
		try {
			return makePost(url, headers, makeStrBody(params), makeStrBody(body), timeout);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cc.joymaker.weiop.service.HttpService#syncHttpPost(java.lang.String,
	 * java.util.Map, java.lang.String, int)
	 */
	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, String body, int timeoutInMs) {
		Throwable cause = null;
		try {
			return makePost(url, headers, null, body, timeoutInMs);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, Object> body, int timeoutInMs, String mimeType) {
		Throwable cause = null;
		try {
			return makeFormPost(url, headers, body, timeoutInMs, mimeType);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPost(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, File file, int timeout) {
		Throwable cause = null;
		try {
			return makeStreamPost(url, headers, makeStrBody(params), body, file, timeout);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	protected HttpResult makePost(final String url, final Map<String, String> headers, final String params, final String body, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}

					StringBuffer sb = new StringBuffer();
					final String urlConnection;
					if (StringUtils.isNotBlank(params)) {
						urlConnection = sb.append(url).append("?").append(params).toString();
					} else {
						urlConnection = sb.append(url).toString();
					}

					HttpPost post = new HttpPost(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					StringEntity entity = new StringEntity(body, Consts.UTF_8);
					entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + url + ",body:" + body);

					CloseableHttpResponse res = client.execute(post);
					try {
						int code = res.getStatusLine().getStatusCode();
						String content = EntityUtils.toString(res.getEntity());
						log.info("Res::" + code + "::" + content);
						return new HttpResult(code, content);
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected HttpResult makeNoParamsPost(final String url, final Map<String, String> headers, final String body, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}

					HttpPost post = new HttpPost(url);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					StringEntity entity = new StringEntity(body, Consts.UTF_8);
					entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + url + ",body:" + body);

					CloseableHttpResponse res = client.execute(post);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected HttpResult makeFormPost(final String url, final Map<String, String> headers, final Map<String, String> body, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					HttpPost post = new HttpPost(url);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					List<NameValuePair> formparams = new ArrayList<NameValuePair>();
					if (body != null) {
						for (String key : body.keySet()) {
							formparams.add(new BasicNameValuePair(key, body.get(key)));
						}
					}

					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
					entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + url + ",body:" + body);

					CloseableHttpResponse res = client.execute(post);
					try {
						String content = EntityUtils.toString(res.getEntity(), Consts.UTF_8);
						log.info(res.getStatusLine().getStatusCode() + "|" + content);
						return new HttpResult(res.getStatusLine().getStatusCode(), content);
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected HttpResult makeFormPost(final String url, final Map<String, String> headers, final Map<String, Object> body, final int timeout,
			final String mimeType) throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					HttpPost post = new HttpPost(url);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					post.setHeader("Content-Type", mimeType);

					String bodys = JSONUtils.valueToString(body);
					StringEntity entity = new StringEntity(bodys, "UTF-8");

					// UrlEncodedFormEntity entity = new
					// UrlEncodedFormEntity(formparams, "UTF-8");
					// entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + url + ",body:" + bodys);

					CloseableHttpResponse res = client.execute(post);
					try {
						String content = EntityUtils.toString(res.getEntity(), Consts.UTF_8);
						log.info(res.getStatusLine().getStatusCode() + "|" + content);
						return new HttpResult(res.getStatusLine().getStatusCode(), content);
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected HttpResult makeStreamPost(final String url, final Map<String, String> headers, final String params, final Map<String, String> body,
			final File file, final int timeout) throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					StringBuffer sb = new StringBuffer();
					final String urlConnection;
					if (StringUtils.isNotBlank(params)) {
						urlConnection = sb.append(url).append("?").append(params).toString();
					} else {
						urlConnection = sb.append(url).toString();
					}

					HttpPost post = new HttpPost(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					if (body != null) {
						for (Map.Entry<String, String> entry : body.entrySet()) {
							builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.TEXT_PLAIN));
						}
					}

					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
					post.setEntity(builder.build());

					CloseableHttpResponse res = client.execute(post);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected String makeStrBody(Map<String, String> body) {
		if (body == null || body.isEmpty()) {
			return URLEncodedUtils.format(new ArrayList<NameValuePair>(), Consts.UTF_8);
		}
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (String key : body.keySet()) {
			list.add(new BasicNameValuePair(key, body.get(key)));
		}
		return URLEncodedUtils.format(list, "utf-8");
	}

	protected List<NameValuePair> makeFormParams(Map<String, String> body) {
		if (body == null || body.isEmpty()) {
			return new ArrayList<NameValuePair>();
		}
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : body.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return list;

	}

	/**
	 * 下面是 http get请求
	 */
	@Override
	public HttpResult syncHttpGet(String url, Map<String, String> headers, Map<String, String> params, int timeoutInMs) {
		Throwable cause = null;
		try {
			return makeGet(url, headers, makeParams(params), timeoutInMs);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpGet(String url, Map<String, String> headers, int timeoutInMs) {
		Throwable cause = null;
		try {
			return makeGet(url, headers, null, timeoutInMs);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpStreamResult syncHttpStreamGet(String url, final Map<String, String> headers, Map<String, String> params, final OutputStream os, int timeout) {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		String paramUrl = makeParams(params);
		StringBuffer sb = new StringBuffer();
		final String urlConnection;
		if (StringUtils.isNotBlank(paramUrl)) {
			urlConnection = sb.append(url).append("?").append(paramUrl).toString();
		} else {
			urlConnection = sb.append(url).toString();
		}

		Future<HttpStreamResult> future = threadPool.submit(new Callable<HttpStreamResult>() {

			public HttpStreamResult call() throws Exception {
				CloseableHttpClient client = null;

				try {
					if (urlConnection.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					HttpGet get = new HttpGet(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							get.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + urlConnection);

					CloseableHttpResponse res = client.execute(get);

					try {
						long contentLength = res.getEntity().getContent().available();
						log.info("length:" + contentLength);
						IOUtils.copy(res.getEntity().getContent(), os);
						return new HttpStreamResult(res.getStatusLine().getStatusCode(), null);
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return new HttpStreamResult(500, null);
		}
	}

	protected HttpResult makeGet(final String url, final Map<String, String> headers, final String params, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		StringBuffer sb = new StringBuffer();
		final String urlConnection;
		if (StringUtils.isNotBlank(params)) {
			urlConnection = sb.append(url).append("?").append(params).toString();
		} else {
			urlConnection = sb.append(url).toString();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					HttpGet get = new HttpGet(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							get.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + urlConnection);
					System.out.println("url:" + urlConnection);

					CloseableHttpResponse res = client.execute(get);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected String makeParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return URLEncodedUtils.format(new ArrayList<NameValuePair>(), Consts.UTF_8);
		}
		StringBuffer sb = new StringBuffer();
		for (String key : params.keySet()) {
			try {
				sb.append(key).append("=").append(URLEncoder.encode((params.get(key) == null) ? "" : params.get(key), Consts.UTF_8.name())).append("&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * HTTP DELETE METHOD
	 */
	@Override
	public HttpResult syncHttpDelete(String url, Map<String, String> headers, Map<String, String> params, int timeout) {
		Throwable cause = null;
		try {
			return makeDelete(url, headers, makeStrBody(params), timeout);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	protected HttpResult makeDelete(final String url, final Map<String, String> headers, final String params, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {
			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					String _url = url;
					if (!StringUtils.isBlank(params)) {
						_url += "?" + params;
					}
					HttpDelete delete = new HttpDelete(_url);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							delete.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + _url);

					CloseableHttpResponse res = client.execute(delete);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	/**
	 * HTTP PUT METHOD
	 */
	@Override
	public HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params, Map<String, String> body, int timeout) {
		Throwable cause = null;
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(makeFormParams(body), Consts.UTF_8);
			log.info("url:" + url + "?" + params + ",body:" + body);
			return makePut(url, headers, makeStrBody(params), entity, timeout);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}
		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPut(String url, Map<String, String> headers, Map<String, String> params, String body, int timeout) {
		Throwable cause = null;
		try {
			StringEntity entity = new StringEntity(body, Consts.UTF_8);
			log.info("url:" + url + "?" + params + ",body:" + body);
			return makePut(url, headers, makeStrBody(params), entity, timeout);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	protected HttpResult makePut(final String url, final Map<String, String> headers, final String params, final HttpEntity entity, final int timeout)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.threadPool == null) {
			makeThreadPool();
		}

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				try {
					if (url.trim().toLowerCase().startsWith("https")) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					String _url = url;
					if (params != null && params.length() > 0)
						_url = url + "?" + params;
					HttpPut put = new HttpPut(_url);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							put.addHeader(header.getKey(), header.getValue());
						}
					}
					for (Header header : put.getAllHeaders()) {
						System.out.println("header name:" + header.getName() + ",value:" + header.getValue());
					}
					put.setEntity(entity);
					CloseableHttpResponse res = client.execute(put);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		return future.get(timeout, TimeUnit.MILLISECONDS);
	}

	protected synchronized void makeThreadPool() {
		int poolSize = 50;
		this.threadPool = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	public HttpResult syncHttpGet(String url, final Map<String, String> headers, Map<String, String> param, int timeout, final boolean httpsFlag) {
		Throwable cause = null;
		try {
			if (this.threadPool == null) {
				makeThreadPool();
			}
			String params = makeParams(param);
			StringBuffer sb = new StringBuffer();
			final String urlConnection;
			if (StringUtils.isNotBlank(params)) {
				urlConnection = sb.append(url).append("?").append(params).toString();
			} else {
				urlConnection = sb.append(url).toString();
			}

			Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

				public HttpResult call() throws Exception {
					CloseableHttpClient client = null;
					if (httpsFlag) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}

					try {

						HttpGet get = new HttpGet(urlConnection);
						if (headers != null) {
							for (Map.Entry<String, String> header : headers.entrySet()) {
								get.addHeader(header.getKey(), header.getValue());
							}
						}

						log.info("url:" + urlConnection);

						CloseableHttpResponse res = client.execute(get);
						try {
							return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
						} finally {
							res.close();
						}
					} catch (Exception ex) {
						throw ex;
					} finally {
						client.close();
					}
				}
			});

			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPost(final String url, final Map<String, String> headers, Map<String, String> param, final Map<String, String> bodys, int timeout,
			final boolean httpsFlag) {

		Throwable cause = null;
		if (this.threadPool == null) {
			makeThreadPool();
		}

		final String params = makeParams(param);
		final String body = makeStrBody(bodys);

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				if (httpsFlag) {
					client = getHttpsClient();
				} else {
					client = getHttpClient();
				}
				try {

					StringBuffer sb = new StringBuffer();
					final String urlConnection;
					if (StringUtils.isNotBlank(params)) {
						urlConnection = sb.append(url).append("?").append(params).toString();
					} else {
						urlConnection = sb.append(url).toString();
					}

					HttpPost post = new HttpPost(urlConnection);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							post.addHeader(header.getKey(), header.getValue());
						}
					}

					StringEntity entity = new StringEntity(body, Consts.UTF_8);
					entity.setContentType("application/x-www-form-urlencoded");

					post.setEntity(entity);

					log.info("url:" + url + ",body:" + body);

					CloseableHttpResponse res = client.execute(post);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});

		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPost(final String url, final Map<String, String> headers, Map<String, String> param, final Map<String, String> body,
			final File file, int timeout, final boolean httpsFlag) {
		Throwable cause = null;
		try {
			if (this.threadPool == null) {
				makeThreadPool();
			}

			final String params = makeParams(param);

			final StringEntity entity = new StringEntity(makeStrBody(body), Consts.UTF_8);

			log.info("url:" + url + "?" + params + ",body:" + body);

			Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

				public HttpResult call() throws Exception {
					CloseableHttpClient client = null;
					if (httpsFlag) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					try {
						String _url = url;
						if (params != null && params.length() > 0)
							_url = url + "?" + params;
						HttpPost post = new HttpPost(_url);
						if (headers != null) {
							for (Map.Entry<String, String> header : headers.entrySet()) {
								post.addHeader(header.getKey(), header.getValue());
							}
						}
						for (Header header : post.getAllHeaders()) {
							System.out.println("header name:" + header.getName() + ",value:" + header.getValue());
						}
						post.setEntity(entity);

						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
						if (body != null) {
							for (Map.Entry<String, String> entry : body.entrySet()) {
								builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.TEXT_PLAIN));
							}
						}

						builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addBinaryBody("content", file, ContentType.APPLICATION_OCTET_STREAM,
								file.getName());
						post.setEntity(builder.build());

						CloseableHttpResponse res = client.execute(post);
						try {
							return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
						} finally {
							res.close();
						}

					} catch (Exception ex) {
						throw ex;
					} finally {
						client.close();
					}
				}
			});

			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPut(final String url, final Map<String, String> headers, Map<String, String> param, Map<String, String> body, int timeout,
			final boolean httpsFlag) {
		Throwable cause = null;
		try {
			if (this.threadPool == null) {
				makeThreadPool();
			}

			final String params = makeParams(param);

			final StringEntity entity = new StringEntity(makeStrBody(body), Consts.UTF_8);

			log.info("url:" + url + "?" + params + ",body:" + body);

			Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

				public HttpResult call() throws Exception {
					CloseableHttpClient client = null;
					if (httpsFlag) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					try {
						String _url = url;
						if (params != null && params.length() > 0)
							_url = url + "?" + params;
						HttpPut put = new HttpPut(_url);
						if (headers != null) {
							for (Map.Entry<String, String> header : headers.entrySet()) {
								put.addHeader(header.getKey(), header.getValue());
							}
						}
						for (Header header : put.getAllHeaders()) {
							System.out.println("header name:" + header.getName() + ",value:" + header.getValue());
						}
						put.setEntity(entity);
						CloseableHttpResponse res = client.execute(put);
						try {
							return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
						} finally {
							res.close();
						}
					} catch (Exception ex) {
						throw ex;
					} finally {
						client.close();
					}
				}
			});

			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpPut(final String url, final Map<String, String> headers, Map<String, String> param, final Map<String, String> body,
			final File file, int timeout, final boolean httpsFlag) {
		Throwable cause = null;
		try {
			if (this.threadPool == null) {
				makeThreadPool();
			}

			final String params = makeParams(param);

			final StringEntity entity = new StringEntity(makeStrBody(body), Consts.UTF_8);

			log.info("url:" + url + "?" + params + ",body:" + body);

			Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

				public HttpResult call() throws Exception {
					CloseableHttpClient client = null;
					if (httpsFlag) {
						client = getHttpsClient();
					} else {
						client = getHttpClient();
					}
					try {
						String _url = url;
						if (params != null && params.length() > 0)
							_url = url + "?" + params;
						HttpPut put = new HttpPut(_url);
						if (headers != null) {
							for (Map.Entry<String, String> header : headers.entrySet()) {
								put.addHeader(header.getKey(), header.getValue());
							}
						}
						for (Header header : put.getAllHeaders()) {
							System.out.println("header name:" + header.getName() + ",value:" + header.getValue());
						}
						put.setEntity(entity);

						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
						if (body != null) {
							for (Map.Entry<String, String> entry : body.entrySet()) {
								builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.TEXT_PLAIN));
							}
						}

						builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addBinaryBody("content", file, ContentType.APPLICATION_OCTET_STREAM,
								file.getName());
						put.setEntity(builder.build());

						CloseableHttpResponse res = client.execute(put);
						try {
							return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
						} finally {
							res.close();
						}

					} catch (Exception ex) {
						throw ex;
					} finally {
						client.close();
					}
				}
			});

			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

	@Override
	public HttpResult syncHttpDelete(final String url, final Map<String, String> headers, Map<String, String> param, int timeout, final boolean httpsFlag) {
		Throwable cause = null;
		if (this.threadPool == null) {
			makeThreadPool();
		}

		final String params = makeParams(param);

		Future<HttpResult> future = threadPool.submit(new Callable<HttpResult>() {

			public HttpResult call() throws Exception {
				CloseableHttpClient client = null;
				if (httpsFlag) {
					client = getHttpsClient();
				} else {
					client = getHttpClient();
				}
				try {
					HttpDelete delete = new HttpDelete(url + "?" + params);
					if (headers != null) {
						for (Map.Entry<String, String> header : headers.entrySet()) {
							delete.addHeader(header.getKey(), header.getValue());
						}
					}

					log.info("url:" + url + "?" + params);

					CloseableHttpResponse res = client.execute(delete);
					try {
						return new HttpResult(res.getStatusLine().getStatusCode(), EntityUtils.toString(res.getEntity(), Consts.UTF_8));
					} finally {
						res.close();
					}
				} catch (Exception ex) {
					throw ex;
				} finally {
					client.close();
				}
			}
		});
		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			cause = (e.getCause() == null) ? e : e.getCause();
			log.error(e.getMessage(), e);
		}

		return new HttpResult(500, (cause.getMessage() == null || cause.getMessage().equals("")) ? "请求" + url + "命令失败" : cause.getMessage());
	}

}