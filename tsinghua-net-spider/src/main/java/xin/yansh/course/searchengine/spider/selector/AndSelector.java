package xin.yansh.course.searchengine.spider.selector;

import xin.yansh.course.searchengine.spider.selector.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * All selectors will be arranged as a pipeline. <br>
 * The next selector uses the result of the previous as source.
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class AndSelector implements xin.yansh.course.searchengine.spider.selector.Selector {

    private List<xin.yansh.course.searchengine.spider.selector.Selector> selectors = new ArrayList<xin.yansh.course.searchengine.spider.selector.Selector>();

    public AndSelector(xin.yansh.course.searchengine.spider.selector.Selector... selectors) {
        for (xin.yansh.course.searchengine.spider.selector.Selector selector : selectors) {
            this.selectors.add(selector);
        }
    }

    public AndSelector(List<xin.yansh.course.searchengine.spider.selector.Selector> selectors) {
        this.selectors = selectors;
    }

    @Override
    public String select(String text) {
        for (xin.yansh.course.searchengine.spider.selector.Selector selector : selectors) {
            if (text == null) {
                return null;
            }
            text = selector.select(text);
        }
        return text;
    }

    @Override
    public List<String> selectList(String text) {
        List<String> results = new ArrayList<String>();
        boolean first = true;
        for (Selector selector : selectors) {
            if (first) {
                results = selector.selectList(text);
                first = false;
            } else {
                List<String> resultsTemp = new ArrayList<String>();
                for (String result : results) {
                    resultsTemp.addAll(selector.selectList(result));
                }
                results = resultsTemp;
                if (results == null || results.size() == 0) {
                    return results;
                }
            }
        }
        return results;
    }
}
