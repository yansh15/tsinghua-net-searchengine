package xin.yansh.course.searchengine.spider.pipeline;

import xin.yansh.course.searchengine.spider.ResultItems;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.pipeline.ConsolePipeline;
import xin.yansh.course.searchengine.spider.pipeline.FilePipeline;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see ConsolePipeline
 * @see FilePipeline
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task task
     */
    public void process(ResultItems resultItems, Task task);
}
