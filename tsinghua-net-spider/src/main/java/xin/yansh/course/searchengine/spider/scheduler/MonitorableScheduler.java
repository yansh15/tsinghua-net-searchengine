package xin.yansh.course.searchengine.spider.scheduler;

import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.scheduler.Scheduler;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {

    public int getLeftRequestsCount(Task task);

    public int getTotalRequestsCount(Task task);

}