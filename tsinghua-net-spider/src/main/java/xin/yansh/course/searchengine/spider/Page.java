package xin.yansh.course.searchengine.spider;

import org.apache.commons.lang3.StringUtils;
import xin.yansh.course.searchengine.spider.selector.Html;
import xin.yansh.course.searchengine.spider.selector.Json;
import xin.yansh.course.searchengine.spider.selector.Selectable;
import xin.yansh.course.searchengine.spider.utils.HttpConstant;
import xin.yansh.course.searchengine.spider.utils.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Page {

    private Request request;

    private ResultItems resultItems = new ResultItems();

    private Html html;

    private Json json;

    private String rawText;

    private Selectable url;

    private Map<String,List<String>> headers;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private boolean downloadSuccess = true;

    private byte[] bytes;

    private List<String> targetUrls = new ArrayList<>();

    private String charset;
    
    public Page() {
    }

    public static Page fail(){
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    public Page setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    public Html getHtml() {
        if (html == null) {
            html = new Html(rawText, request.getUrl());
        }
        return html;
    }

    public Json getJson() {
        if (json == null) {
            json = new Json(rawText);
        }
        return json;
    }

    public void setHtml(Html html) {
        this.html = html;
    }

    public List<String> getTargetUrls() {
        return targetUrls;
    }

    public void addTargetUrls(List<String> tUrls) {
        for (String s : tUrls) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetUrls.add(s);
        }
    }

    public void addTargetUrl(String tUrl) {
        if (StringUtils.isBlank(tUrl) || tUrl.equals("#")) {
            return;
        }
        tUrl = UrlUtils.canonicalizeUrl(tUrl, url.toString());
        targetUrls.add(tUrl);
    }

    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRawText() {
        return rawText;
    }

    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + resultItems +
                ", html=" + html +
                ", json=" + json +
                ", rawText='" + rawText + '\'' +
                ", url=" + url +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                ", downloadSuccess=" + downloadSuccess +
                ", targetUrls=" + targetUrls +
                ", charset='" + charset + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
