package xin.yansh.course.searchengine.spider.scheduler.component;

import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.scheduler.component.DuplicateRemover;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(String url, Task task) {
        return !urls.add(url);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }
}
