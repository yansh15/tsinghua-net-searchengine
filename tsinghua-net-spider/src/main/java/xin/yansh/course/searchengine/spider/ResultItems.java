package xin.yansh.course.searchengine.spider;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultItems {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private xin.yansh.course.searchengine.spider.Request request;

    private boolean skip;

    public <T> T get(String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getAll() {
        return fields;
    }

    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }

    public xin.yansh.course.searchengine.spider.Request getRequest() {
        return request;
    }

    public ResultItems setRequest(Request request) {
        this.request = request;
        return this;
    }

    public boolean isSkip() {
        return skip;
    }

    public ResultItems setSkip(boolean skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public String toString() {
        return "ResultItems{" +
                "fields=" + fields +
                ", request=" + request +
                ", skip=" + skip +
                '}';
    }
}
