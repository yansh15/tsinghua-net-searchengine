package xin.yansh.course.searchengine.spider.pipeline;

import xin.yansh.course.searchengine.spider.ResultItems;
import xin.yansh.course.searchengine.spider.Task;

import java.util.Map;

public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
