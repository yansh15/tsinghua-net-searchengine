package xin.yansh.course.searchengine.spider.selector;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSelectable implements xin.yansh.course.searchengine.spider.selector.Selectable {

    protected abstract List<String> getSourceTexts();

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable css(String selector) {
        return $(selector);
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable css(String selector, String attrName) {
        return $(selector, attrName);
    }

    protected xin.yansh.course.searchengine.spider.selector.Selectable select(xin.yansh.course.searchengine.spider.selector.Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            String result = selector.select(string);
            if (result != null) {
                results.add(result);
            }
        }
        return new xin.yansh.course.searchengine.spider.selector.PlainText(results);
    }

    protected xin.yansh.course.searchengine.spider.selector.Selectable selectList(xin.yansh.course.searchengine.spider.selector.Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            List<String> result = selector.selectList(string);
            results.addAll(result);
        }
        return new PlainText(results);
    }

    @Override
    public List<String> all() {
        return getSourceTexts();
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable jsonPath(String jsonPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get() {
        if (CollectionUtils.isNotEmpty(all())) {
            return all().get(0);
        } else {
            return null;
        }
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable select(xin.yansh.course.searchengine.spider.selector.Selector selector) {
        return select(selector, getSourceTexts());
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable selectList(Selector selector) {
        return selectList(selector, getSourceTexts());
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable regex(String regex) {
        xin.yansh.course.searchengine.spider.selector.RegexSelector regexSelector = xin.yansh.course.searchengine.spider.selector.Selectors.regex(regex);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public xin.yansh.course.searchengine.spider.selector.Selectable regex(String regex, int group) {
        RegexSelector regexSelector = Selectors.regex(regex, group);
        return selectList(regexSelector, getSourceTexts());
    }

    @Override
    public Selectable replace(String regex, String replacement) {
        xin.yansh.course.searchengine.spider.selector.ReplaceSelector replaceSelector = new ReplaceSelector(regex,replacement);
        return select(replaceSelector, getSourceTexts());
    }

    public String getFirstSourceText() {
        if (getSourceTexts() != null && getSourceTexts().size() > 0) {
            return getSourceTexts().get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean match() {
        return getSourceTexts() != null && getSourceTexts().size() > 0;
    }
}
