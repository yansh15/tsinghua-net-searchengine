package xin.yansh.course.searchengine.spider.processor;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Site;

public interface PageProcessor {
    public void process(Page page);

    public Site getSite();
}
