package com.xinyirun.scm.core.system.serviceimpl.business.track;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 利用Apache HttpClient完成请求
 *
 * @author zhou
 * @contact 电话: 18963752887, QQ: 251915460
 * @create 2015年6月15日 下午2:30:53
 * @version V1.0
 */
public class HttpUtils {
	static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	/**
	 * 使用get方式利用HttpClient请求数据
	 *
	 * @param url
	 * @param params
	 * @param respCharset
	 * @return
	 */
	public static String getRequest(String url, Map<String, String> params, String respCharset){
		return getRequest(url,params,respCharset,30000);
	}

	/**
	 * 使用get方式利用HttpClient请求数据
	 *
	 * @param url
	 * @param params
	 * @param respCharset
	 * @param timeout
	 * @return
	 */
	public static String getRequest(String url, Map<String, String> params, String respCharset,int timeout) {
		HttpClient client = null;
		GetMethod method = null;
		InputStream resStream = null;
		BufferedReader br = null;

		try {
			client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
			client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method = new GetMethod(url);
			method.setRequestHeader("Connection", "close");
			if (params != null) {
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(key);
					if(params.get(key) != null) {
						nvp.setValue(HashUtils.getStringValue(params, key));
						nvList.add(nvp);
					}
				}
				method.setQueryString(nvList.toArray(new NameValuePair[]{}));
			}

			int code = client.executeMethod(method);
			resStream = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(resStream, respCharset));
			StringBuffer resBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = br.readLine()) != null) {
				resBuffer.append(resTemp);
			}

			String response = resBuffer.toString();

			if (code != 200) {
				logger.error("错误状态码:" + code + "\n" + response);
			}

			return response;
		} catch (Exception e) {
			logger.error("", e);
		} finally {

			if (resStream != null) {
				try {
					resStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (method != null) {
				try {
					method.releaseConnection();
					((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
				} catch (Exception e) {
					logger.error("", e);
				}
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}

		}

		return null;
	}



	/**
	 * 使用get方式利用HttpClient请求数据
	 *
	 * @param url
	 * @param params
	 * @param respCharset
	 * @param timeout
	 * @return
	 */
	public static String getRequest(String url,Map<String,String> headers, Map<String, String> params, String respCharset,int timeout) {
		HttpClient client = null;
		GetMethod method = null;
		InputStream resStream = null;
		BufferedReader br = null;

		try {
			client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
			client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method = new GetMethod(url);
			//method.setRequestHeader("Connection", "close");
			if(headers!=null){
				for (String key: headers.keySet()) {
					method.setRequestHeader(key,headers.get(key));
				}
			}

			if (params != null) {
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(key);
					if(params.get(key) != null) {
						nvp.setValue(HashUtils.getStringValue(params, key));
						nvList.add(nvp);
					}
				}
				method.setQueryString(nvList.toArray(new NameValuePair[]{}));
			}

			int code = client.executeMethod(method);
			resStream = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(resStream, respCharset));
			StringBuffer resBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = br.readLine()) != null) {
				resBuffer.append(resTemp);
			}

			String response = resBuffer.toString();

			if (code != 200) {
				logger.error("错误状态码:" + code + "\n" + response);
			}

			return response;
		} catch (Exception e) {
			logger.error("", e);
		} finally {

			if (resStream != null) {
				try {
					resStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (method != null) {
				try {
					method.releaseConnection();
					((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
				} catch (Exception e) {
					logger.error("", e);
				}
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}

		}

		return null;
	}

	/**
	 * 使用post方式利用HttpClient请求数据
	 *
	 * @param url
	 * @param params
	 * @param respCharset
	 * @return
	 */
	public static String post(String url, Map<String, String> params, String respCharset) {
		return post(url,null,params,respCharset);
	}

	/**
	 * post请求
	 *
	 * @param url
	 * @param headers
	 * @param params
	 * @param respCharset
	 * @return
	 */
	public static String post(String url, Map<String, String> headers,Map<String, String> params, String respCharset){
		return post(url,headers,params,respCharset,30000);
	}

	/**
	 * post请求
	 *
	 * @param url
	 * @param headers
	 * @param params
	 * @param respCharset
	 * @param timeout
	 * @return
	 */
	public static String post(String url, Map<String, String> headers,Map<String, String> params, String respCharset,int timeout){
		return post(url,headers,params,respCharset,timeout,null);
	}

	/**
	 * post请求
	 *
	 * @param url
	 * @param headers
	 * @param params
	 * @param respCharset
	 * @param timeout
	 * @param body
	 * @return
	 */
	public static String post(String url, Map<String, String> headers,Map<String, String> params, String respCharset,int timeout, String body) {

		HttpClient client = null;
		PostMethod method = null;
		InputStream resStream = null;
		BufferedReader br = null;

		try {

			client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
			client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method = new PostMethod(url);
			method.setRequestHeader("Connection", "close");

			if (headers != null) {
				for (String key : headers.keySet()) {
					method.setRequestHeader(key,headers.get(key));
				}
			}

			if (params != null) {
				for (String key : params.keySet()) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(key);
					if(params.get(key) != null) {
						nvp.setValue(HashUtils.getStringValue(params, key));
						method.addParameter(nvp);
					}
				}
			}

			if (StringUtils.isNotBlank(body)) {
				// 发送JSON 参数
				method.setRequestHeader("Content-Type", "application/json");
				method.setRequestBody(body);
			}

			int code = client.executeMethod(method);
			resStream = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(resStream, respCharset));
			StringBuffer resBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = br.readLine()) != null) {
				resBuffer.append(resTemp);
			}

			return resBuffer.toString();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (url.startsWith("https")) {
				Protocol.unregisterProtocol("https");
			}

			if (resStream != null) {
				try {
					resStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (method != null) {
				try {
					method.releaseConnection();
					((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
				} catch (Exception e) {
					logger.error("", e);
				}
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
		}

		return null;
	}

	/**
	 * 上传文件
	 *
	 * @param file     文件
	 * @param postname 文件字段名称
	 * @param url      上传url
	 * @return
	 * @version 1.0
	 */
	public static String upload(File file, String postname, String url) {

		PostMethod filePost = null;
		HttpClient client = null;

		try {
			// 通过以下方法可以模拟页面参数提交
			client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
			Part[] parts = {new FilePart(postname, file)};

			filePost = new PostMethod(url);
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(5000);

			int status = client.executeMethod(filePost);
			if (status == HttpStatus.SC_OK) {
				logger.info("上传成功");
				return filePost.getResponseBodyAsString();
			} else {
				logger.error("上传失败");
			}
		} catch (Exception ex) {
			logger.error("上传失败", ex);
		} finally {
			if (url.startsWith("https")) {
				Protocol.unregisterProtocol("https");
			}
			filePost.releaseConnection();
			((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
		}

		return null;
	}

	/**
	 * postBody
	 *
	 * @param url
	 * @param params
	 * @param entity
	 * @param respCharset
	 * @return
	 */
	public static String postBody(String url, Map<String, String> params, RequestEntity entity, String respCharset) {

		HttpClient client = null;
		PostMethod method = null;
		InputStream resStream = null;
		BufferedReader br = null;

		try {

			client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
			client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
			client.getHttpConnectionManager().getParams().setSoTimeout(30000);
			client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method = new PostMethod(url);
			method.setRequestHeader("Connection", "close");
			method.setRequestHeader("Content-Type", "application/json");
			if (params != null) {
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(key);
					if(params.get(key) != null) {
						nvp.setValue(HashUtils.getStringValue(params, key));
						method.addParameter(nvp);
					}
				}
				method.setQueryString(nvList.toArray(new NameValuePair[]{}));
			}

			method.setRequestEntity(entity);

			int code = client.executeMethod(method);
			resStream = method.getResponseBodyAsStream();
			br = new BufferedReader(new InputStreamReader(resStream, respCharset));
			StringBuffer resBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = br.readLine()) != null) {
				resBuffer.append(resTemp);
			}
			String response = resBuffer.toString();

			if (code != 200) {
				return response;
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (url.startsWith("https")) {
				Protocol.unregisterProtocol("https");
			}

			if (resStream != null) {
				try {
					resStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}

			if (method != null) {
				try {
					method.releaseConnection();
					((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
				} catch (Exception e) {
					logger.error("", e);
				}
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
		}

		return null;
	}

}