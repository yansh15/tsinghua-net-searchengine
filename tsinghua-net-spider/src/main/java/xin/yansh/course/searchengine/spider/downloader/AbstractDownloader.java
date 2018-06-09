package xin.yansh.course.searchengine.spider.downloader;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Site;
import xin.yansh.course.searchengine.spider.downloader.Downloader;
import xin.yansh.course.searchengine.spider.selector.Html;

/**
 * Base class of downloader with some common methods.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class AbstractDownloader implements Downloader {

    /**
     * A simple method to download a url.
     *
     * @param url url
     * @return html
     */
    public Html download(String url) {
        return download(url, null);
    }

    /**
     * A simple method to download a url.
     *
     * @param url url
     * @param charset charset
     * @return html
     */
    public Html download(String url, String charset) {
        Page page = download(new Request(url), Site.me().setCharset(charset).toTask());
        return (Html) page.getHtml();
    }

    protected void onSuccess(Request request) {
    }

    protected void onError(Request request) {
    }

}
