package xin.yansh.course.searchengine.spider.scheduler;

import xin.yansh.course.searchengine.spider.Task;

public interface MonitorableScheduler extends Scheduler {
    public int getLeftRequestsCount(Task task);

    public int getTotalRequestsCount(Task task);
}