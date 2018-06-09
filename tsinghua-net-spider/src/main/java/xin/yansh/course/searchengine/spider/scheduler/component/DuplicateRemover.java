package xin.yansh.course.searchengine.spider.scheduler.component;

import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;

/**
 * Remove duplicate requests.
 * @author code4crafer@gmail.com
 * @since 0.5.1
 */
public interface DuplicateRemover {

    public boolean isDuplicate(String url, Task task);

    public void resetDuplicateCheck(Task task);

    public int getTotalRequestsCount(Task task);

}
