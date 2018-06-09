package xin.yansh.course.searchengine.spider.scheduler;

import xin.yansh.course.searchengine.spider.Task;

public interface Scheduler {
    public void push(String request, Task task);

    public String poll(Task task);
}
