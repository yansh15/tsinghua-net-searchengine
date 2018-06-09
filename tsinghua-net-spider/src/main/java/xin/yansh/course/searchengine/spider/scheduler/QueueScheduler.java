package xin.yansh.course.searchengine.spider.scheduler;

import org.apache.http.annotation.ThreadSafe;
import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.scheduler.DuplicateRemovedScheduler;
import xin.yansh.course.searchengine.spider.scheduler.MonitorableScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public void pushWhenNoDuplicate(String url, Task task) {
        queue.add(url);
    }

    @Override
    public String poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
