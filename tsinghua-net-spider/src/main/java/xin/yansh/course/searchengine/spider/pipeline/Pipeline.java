package xin.yansh.course.searchengine.spider.pipeline;

import xin.yansh.course.searchengine.spider.ResultItems;
import xin.yansh.course.searchengine.spider.Task;

public interface Pipeline {
    public void process(ResultItems resultItems, Task task);
}
