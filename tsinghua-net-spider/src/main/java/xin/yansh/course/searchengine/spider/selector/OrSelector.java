package xin.yansh.course.searchengine.spider.selector;

import xin.yansh.course.searchengine.spider.selector.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * All extractors will do extracting separately, <br>
 * and the results of extractors will combined as the final result.
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class OrSelector implements xin.yansh.course.searchengine.spider.selector.Selector {

    private List<xin.yansh.course.searchengine.spider.selector.Selector> selectors = new ArrayList<xin.yansh.course.searchengine.spider.selector.Selector>();

    public OrSelector(xin.yansh.course.searchengine.spider.selector.Selector... selectors) {
        for (xin.yansh.course.searchengine.spider.selector.Selector selector : selectors) {
            this.selectors.add(selector);
        }
    }

    public OrSelector(List<xin.yansh.course.searchengine.spider.selector.Selector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public String select(String text) {
        for (xin.yansh.course.searchengine.spider.selector.Selector selector : selectors) {
            String result = selector.select(text);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public List<String> selectList(String text) {
        List<String> results = new ArrayList<String>();
        for (Selector selector : selectors) {
            List<String> strings = selector.selectList(text);
            results.addAll(strings);
        }
        return results;
    }
}
