package xin.yansh.course.searchengine.spider.scheduler;

import xin.yansh.course.searchengine.spider.Request;
import xin.yansh.course.searchengine.spider.Task;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Scheduler {

    public void push(String request, Task task);

    public String poll(Task task);

}
