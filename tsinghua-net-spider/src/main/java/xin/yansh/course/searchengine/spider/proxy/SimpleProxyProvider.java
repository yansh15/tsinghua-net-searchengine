package xin.yansh.course.searchengine.spider.proxy;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleProxyProvider implements ProxyProvider {

    private final List<xin.yansh.course.searchengine.spider.proxy.Proxy> proxies;

    private final AtomicInteger pointer;

    public SimpleProxyProvider(List<xin.yansh.course.searchengine.spider.proxy.Proxy> proxies) {
        this(proxies, new AtomicInteger(-1));
    }

    private SimpleProxyProvider(List<xin.yansh.course.searchengine.spider.proxy.Proxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

    public static SimpleProxyProvider from(xin.yansh.course.searchengine.spider.proxy.Proxy... proxies) {
        List<xin.yansh.course.searchengine.spider.proxy.Proxy> proxiesTemp = new ArrayList<xin.yansh.course.searchengine.spider.proxy.Proxy>(proxies.length);
        for (xin.yansh.course.searchengine.spider.proxy.Proxy proxy : proxies) {
            proxiesTemp.add(proxy);
        }
        return new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }

    @Override
    public void returnProxy(xin.yansh.course.searchengine.spider.proxy.Proxy proxy, Page page, Task task) {
    }

    @Override
    public Proxy getProxy(Task task) {
        return proxies.get(incrForLoop());
    }

    private int incrForLoop() {
        int p = pointer.incrementAndGet();
        int size = proxies.size();
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }
}
