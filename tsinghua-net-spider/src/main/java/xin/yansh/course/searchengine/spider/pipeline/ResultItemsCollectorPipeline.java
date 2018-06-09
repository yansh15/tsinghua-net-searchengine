package xin.yansh.course.searchengine.spider.pipeline;

import xin.yansh.course.searchengine.spider.ResultItems;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.pipeline.CollectorPipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> collector = new ArrayList<ResultItems>();

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        collector.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }
}
