package xin.yansh.course.searchengine.spider.selector;

import xin.yansh.course.searchengine.spider.selector.*;
import xin.yansh.course.searchengine.spider.selector.Selector;

/**
 * Convenient methods for selectors.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public abstract class Selectors {

    public static RegexSelector regex(String expr) {
        return new RegexSelector(expr);
    }

    public static RegexSelector regex(String expr, int group) {
        return new RegexSelector(expr,group);
    }

    public static CssSelector $(String expr) {
        return new CssSelector(expr);
    }

    public static CssSelector $(String expr, String attrName) {
        return new CssSelector(expr, attrName);
    }

    public static XpathSelector xpath(String expr) {
        return new XpathSelector(expr);
    }

    /**
     * @see #xpath(String)
     * @param expr expr
     * @return new selector
     */
    @Deprecated
    public static XpathSelector xsoup(String expr) {
        return new XpathSelector(expr);
    }

    public static AndSelector and(xin.yansh.course.searchengine.spider.selector.Selector... selectors) {
        return new AndSelector(selectors);
    }

    public static OrSelector or(Selector... selectors) {
        return new OrSelector(selectors);
    }

}