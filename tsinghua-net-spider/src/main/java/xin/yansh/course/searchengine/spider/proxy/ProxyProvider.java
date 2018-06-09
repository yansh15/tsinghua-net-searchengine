package xin.yansh.course.searchengine.spider.proxy;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Task;

public interface ProxyProvider {
    void returnProxy(xin.yansh.course.searchengine.spider.proxy.Proxy proxy, Page page, Task task);

    Proxy getProxy(Task task);
    
}
