package xin.yansh.course.searchengine.spider.pipeline;

import java.util.List;

public interface CollectorPipeline<T> extends Pipeline {
    public List<T> getCollected();
}
