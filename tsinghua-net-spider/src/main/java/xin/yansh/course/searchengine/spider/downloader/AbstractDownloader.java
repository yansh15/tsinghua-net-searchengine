package xin.yansh.course.searchengine.spider.downloader;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Site;
import xin.yansh.course.searchengine.spider.selector.Html;

public abstract class AbstractDownloader implements Downloader {

    public Html download(String url) {
        return download(url, null);
    }

    public Html download(String url, String charset) {
        Page page = download(new Request(url), Site.me().setCharset(charset).toTask());
        return (Html) page.getHtml();
    }

    protected void onSuccess(Request request) {
    }

    protected void onError(Request request) {
    }

}
