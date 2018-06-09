package xin.yansh.course.searchengine.spider.scheduler.component;

import xin.yansh.course.searchengine.spider.Task;

public interface DuplicateRemover {
    public boolean isDuplicate(String url, Task task);

    public void resetDuplicateCheck(Task task);

    public int getTotalRequestsCount(Task task);

}
