package com.letv.mocker.framework.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.letv.mocker.framework.exception.CallHttpResultException;

@SuppressWarnings("deprecation")
public class HttpClientUtil {

	private static final String CHARSET = "utf-8";
	private static HttpClient client;
	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 1000;
	/**
	 * 每个路由最大连接数
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 400;
	/**
	 * 获取连接的最大等待时间
	 */
	public final static int WAIT_TIMEOUT = 10000;
	/**
	 * socket连接超时时间
	 */
	public final static int CONNECT_TIMEOUT = 10000;
	/**
	 * 请求超时时间
	 */
	public final static int READ_TIMEOUT = 10000;
	/**
	 * 请求超时时间阈值
	 */
	public final static int MIN_TIME = 200;

	// 15/03/16
	// 解决client对象长期存活导致timeOut无法重新赋值bug:增加一个超时缓存,和下次请求超时对比:若不相等,重新生成一个新httpclient对象
	private static int TIMEOUT_CACHE = 0;

	/**
	 * 生成HTTPClient类
	 * 
	 * @param timeOut
	 *            超时时间
	 *            接口响应时间在200ms内被允许的，超时时间最好设置不要小于阈值200ms,若设置的过小测试无意义;该方法会调整为200ms
	 * @return
	 */
	public static synchronized HttpClient getHttpClient(int timeOut) {

		TIMEOUT_CACHE = TIMEOUT_CACHE == 0 ? timeOut : TIMEOUT_CACHE;

		if (null == client || TIMEOUT_CACHE != timeOut) {
			TIMEOUT_CACHE = timeOut;
			final HttpParams params = new BasicHttpParams();

			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);

			// 设置HttpClient支持HTTP和HTTPS两种模式
			final SchemeRegistry supportedSchemes = new SchemeRegistry();
			final SchemeSocketFactory sf = PlainSocketFactory
					.getSocketFactory();
			supportedSchemes.register(new Scheme("http", 80, sf));
			supportedSchemes.register(new Scheme("https", 443, sf));

			final int perTime = timeOut / 3;
			// 从连接池中取连接的超时时间
			final int wait_timeout = timeOut < MIN_TIME ? WAIT_TIMEOUT
					: perTime;
			ConnManagerParams.setTimeout(params, wait_timeout);
			// 设置连接超时时间
			final int conn_timeout = timeOut < MIN_TIME ? CONNECT_TIMEOUT
					: perTime;
			HttpConnectionParams.setConnectionTimeout(params, conn_timeout);
			// 设置请求超时时间
			final int so_timeout = timeOut < MIN_TIME ? READ_TIMEOUT : perTime;
			HttpConnectionParams.setSoTimeout(params, so_timeout);

			// 使用线程安全的连接管理来创建HttpClient
			final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
					supportedSchemes);
			// 设置最大连接数
			cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
			// 设置每个路由最大连接数
			cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);

			// 创建一个client对象
			client = new DefaultHttpClient(cm, params);
		}
		return client;
	}

	/**
	 * Get 请求
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 *             /
	 */
	static public byte[] get(String url, int timeOut, String uuid)
			throws CallHttpResultException {
		try {
			final HttpGet get = new HttpGet();
			final HttpResponse response = getHttpResponse(get, url, null, null,
					timeOut);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK
					&& response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
				// 直接终止连接
				get.abort();
				throw new CallHttpResultException("StatusCode is:"
						+ response.getStatusLine().getStatusCode());
			}
			HttpEntity resEntity = response.getEntity();
			// 读取数据并关闭连接
			byte[] br = EntityUtils.toByteArray(resEntity);
			System.out.println(String.format("%s`====HTTPGET  byteLength=%d",
					uuid, br.length));
			return br;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new CallHttpResultException(e.getClass().getName() + ":"
					+ e.getMessage());
		}
	}

	/**
	 * Post 请求
	 */
	public static byte[] post(String url, Map<String, String> parameter,
			String codingType, int timeOut) throws CallHttpResultException {
		try {
			final HttpPost post = new HttpPost();
			final HttpResponse response = getHttpResponse(post, url, parameter,
					codingType, timeOut);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK
					&& response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
				post.abort();
				throw new Exception("StatusCode is:"
						+ response.getStatusLine().getStatusCode());
			}
			final HttpEntity resEntity = response.getEntity();
			byte[] br = EntityUtils.toByteArray(resEntity);
			System.out.println(String.format("====HTTP POST  byteLength=%d",
					br.length));
			// 转换结果集格式 为byte数组
			return br;
		} catch (final Exception e) {
			System.out.println("IOException:" + e.toString());
			throw new CallHttpResultException(e.getClass().getName() + ":"
					+ e.getMessage());
		}
	}

	/**
	 * 获取HTTP状态码
	 * 
	 * @param method
	 *            GET POST
	 * @param url
	 * @param paramter
	 *            post请求携带参数
	 * @param codingType
	 *            post请求body字符集
	 * @param timeOut
	 * @param uuid
	 */
	public static int getHttpStatusCode(HttpRequestBase request, String url,
			Map<String, String> parameter, String codingType, int timeOut)
			throws Exception {
		final HttpResponse response = getHttpResponse(request, url, parameter,
				codingType, timeOut);
		request.abort();
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * get或post请求获取httpResponse
	 * 
	 * @param method
	 *            "GET"/"POST"
	 * @param get
	 *            method=GET起作用
	 * @param post
	 *            method=POST起作用
	 * @param url
	 *            请求url
	 * @param parameter
	 *            post请求参数,method=POST起作用
	 * @param requestCodingType
	 *            post请求实体字符集
	 * @param timeOut
	 *            请求超时时间
	 * @param uuid
	 * @return response 响应实体
	 */
	public static HttpResponse getHttpResponse(HttpRequestBase request,
			String url, Map<String, String> parameter,
			String requestCodingType, int timeOut)
			throws CallHttpResultException {
		try {
			request.setURI(new URI(url.trim()));
			if (request instanceof HttpPost) {
				// 添加请求参数
				final List<NameValuePair> params = new ArrayList<NameValuePair>();
				final Iterator<Entry<String, String>> iter = parameter
						.entrySet().iterator();
				while (iter.hasNext()) {
					final Entry<String, String> entry = iter.next();
					params.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				// 设置字符集
				final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						params, requestCodingType);
				((HttpPost) request).setEntity(entity);
			} else if (!(request instanceof HttpGet)) {
				throw new Exception("Temporarily does not support HTTP method="
						+ request.getMethod().toUpperCase());
			}
			// 发送请求
			final HttpClient client = getHttpClient(timeOut);
			return client.execute(request);
		} catch (final Exception e) {
			throw new CallHttpResultException(e.getClass().getName() + ":"
					+ e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String httpsPost(String url, Map<String, String> map,
			String charset) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			if (map != null) {
				Iterator iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> elem = (Entry<String, String>) iterator
							.next();
					list.add(new BasicNameValuePair(elem.getKey(), elem
							.getValue()));
				}
				if (list.size() > 0) {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							list, charset);
					httpPost.setEntity(entity);
				}
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
