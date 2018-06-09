package xin.yansh.course.searchengine.spider;

public interface SpiderListener {
    public void onSuccess(xin.yansh.course.searchengine.spider.Request request);

    public void onError(Request request);
}
