package xin.yansh.course.searchengine.spider.downloader;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;

public interface Downloader {
    public Page download(Request request, Task task);

    public void setThread(int threadNum);
}
