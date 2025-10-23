package com.xinyirun.scm.ai.workflow.node.httprequest;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * 工作流HTTP请求节点
 *
 * 此节点负责发送HTTP请求并处理响应。
 * 支持多种请求方法和内容类型。
 */
@Slf4j
public class HttpRequestNode extends AbstractWfNode {

    private static final String FORM_DATA_BOUNDARY_PRE = "----WebKitFormBoundary";

    public HttpRequestNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity node, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        List<NodeIOData> outputData = new ArrayList<>();

        HttpRequestNodeConfig nodeConfig = checkAndGetConfig(HttpRequestNodeConfig.class);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(nodeConfig.getTimeout() * 1000)
                .setConnectTimeout(nodeConfig.getTimeout() * 1000)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(nodeConfig.getRetryTimes(), true))
                .build()) {

            String url = appendParams(nodeConfig.getUrl(), nodeConfig.getParams());
            String contentType = nodeConfig.getContentType();
            HttpUriRequest httpRequest;

            if (HttpGet.METHOD_NAME.equalsIgnoreCase(nodeConfig.getMethod())) {
                httpRequest = new HttpGet(url);
                httpRequest.setHeader(CONTENT_TYPE, contentType);
                setHeaders(httpRequest, nodeConfig.getHeaders());
            } else if (HttpPost.METHOD_NAME.equalsIgnoreCase(nodeConfig.getMethod())) {
                HttpPost httpPost = new HttpPost(url);
                httpRequest = httpPost;
                httpRequest.setHeader(CONTENT_TYPE, contentType);
                setHeaders(httpRequest, nodeConfig.getHeaders());

                if (contentType.equalsIgnoreCase("text/plain")) {
                    StringEntity textEntity = new StringEntity(nodeConfig.getTextBody(), ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8));
                    httpPost.setEntity(textEntity);
                } else if (contentType.equalsIgnoreCase("application/json")) {
                    StringEntity jsonEntity = new StringEntity(JsonUtil.toJson(nodeConfig.getJsonBody()), ContentType.APPLICATION_JSON.withCharset(Consts.UTF_8));
                    httpPost.setEntity(jsonEntity);
                } else if (contentType.equalsIgnoreCase("multipart/form-data")) {
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    if (nodeConfig.getFormDataBody() != null) {
                        for (HttpRequestNodeConfig.Param entry : nodeConfig.getFormDataBody()) {
                            if (entry.getValue() instanceof File) {
                                entityBuilder.addPart(entry.getName(), new FileBody((File) entry.getValue()));
                            } else {
                                entityBuilder.addTextBody(entry.getName(), entry.getValue().toString());
                            }
                        }
                    }
                    String boundary = FORM_DATA_BOUNDARY_PRE + System.currentTimeMillis();
                    entityBuilder.setBoundary(boundary);
                    httpPost.setEntity(entityBuilder.build());
                    httpRequest.setHeader(CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
                } else if (contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
                    StringEntity formEntity = new StringEntity(JsonUtil.toJson(nodeConfig.getFormUrlencodedBody()), ContentType.APPLICATION_FORM_URLENCODED.withCharset(Consts.UTF_8));
                    httpPost.setEntity(formEntity);
                }
            } else {
                log.error("不支持的请求方式: {}", nodeConfig.getMethod());
                throw new RuntimeException("不支持的HTTP请求方式");
            }

            try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), Consts.UTF_8);

                if (Boolean.TRUE.equals(nodeConfig.getClearHtml())) {
                    Document doc = Jsoup.parse(responseBody);
                    responseBody = doc.body().text();
                }

                if (statusCode == 200) {
                    NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "响应内容", responseBody);
                    outputData.add(output);
                } else {
                    log.error("HTTP请求失败，状态码: {}", statusCode);
                    NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "错误内容", responseBody);
                    outputData.add(output);
                }

                outputData.add(NodeIOData.createByText("status_code", "HTTP状态码", String.valueOf(statusCode)));
            }
        } catch (IOException e) {
            log.error("HTTP请求发生异常: {}", e.getMessage(), e);
        }

        return NodeProcessResult.builder().content(outputData).build();
    }

    /**
     * 将查询参数附加到URL上
     *
     * @param url URL地址
     * @param params 查询参数列表
     * @return 附加参数后的URL
     */
    private String appendParams(String url, List<HttpRequestNodeConfig.Param> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder paramBuilder = new StringBuilder();
        Iterator<HttpRequestNodeConfig.Param> iterator = params.iterator();
        while (iterator.hasNext()) {
            HttpRequestNodeConfig.Param param = iterator.next();
            String value = param.getValue().toString();
            paramBuilder.append("&").append(param.getName()).append("=").append(value);
        }

        String paramString = paramBuilder.toString();
        if (StringUtils.isBlank(paramString)) {
            return url;
        }

        if (url.contains("?")) {
            return url + paramString;
        } else {
            return url + "?" + paramString.substring(1);
        }
    }

    /**
     * 设置HTTP请求头
     *
     * @param httpRequest HTTP请求对象
     * @param headers 请求头参数列表
     */
    private void setHeaders(HttpUriRequest httpRequest, List<HttpRequestNodeConfig.Param> headers) {
        if (headers == null) {
            return;
        }
        for (HttpRequestNodeConfig.Param header : headers) {
            httpRequest.addHeader(header.getName(), header.getValue().toString());
        }
    }
}
