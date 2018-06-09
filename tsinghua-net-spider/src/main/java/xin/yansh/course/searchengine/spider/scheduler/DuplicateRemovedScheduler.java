package xin.yansh.course.searchengine.spider.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.scheduler.Scheduler;
import xin.yansh.course.searchengine.spider.scheduler.component.DuplicateRemover;
import xin.yansh.course.searchengine.spider.scheduler.component.HashSetDuplicateRemover;
import xin.yansh.course.searchengine.spider.utils.HttpConstant;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(String url, Task task) {
        logger.trace("get a candidate url {}", url);
        if (!duplicatedRemover.isDuplicate(url, task)) {
            logger.debug("push to queue {}", url);
            pushWhenNoDuplicate(url, task);
        }
    }

    protected void pushWhenNoDuplicate(String url, Task task) {

    }
}
