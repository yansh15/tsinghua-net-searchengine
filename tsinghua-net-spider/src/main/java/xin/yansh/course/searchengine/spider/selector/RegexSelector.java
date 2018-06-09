package xin.yansh.course.searchengine.spider.selector;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexSelector implements Selector {

    private String regexStr;

    private Pattern regex;

    private int group = 1;

    public RegexSelector(String regexStr, int group) {
        this.compileRegex(regexStr);
        this.group = group;
    }

    private void compileRegex(String regexStr) {
        if (StringUtils.isBlank(regexStr)) {
            throw new IllegalArgumentException("regex must not be empty");
        }
        try {
            this.regex = Pattern.compile(regexStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            this.regexStr = regexStr;
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("invalid regex "+regexStr, e);
        }
    }

    public RegexSelector(String regexStr) {
        this.compileRegex(regexStr);
        if (regex.matcher("").groupCount() == 0) {
            this.group = 0;
        } else {
            this.group = 1;
        }
    }

    @Override
    public String select(String text) {
        return selectGroup(text).get(group);
    }

    @Override
    public List<String> selectList(String text) {
        List<String> strings = new ArrayList<String>();
        List<xin.yansh.course.searchengine.spider.selector.RegexResult> results = selectGroupList(text);
        for (xin.yansh.course.searchengine.spider.selector.RegexResult result : results) {
            strings.add(result.get(group));
        }
        return strings;
    }

    public xin.yansh.course.searchengine.spider.selector.RegexResult selectGroup(String text) {
        Matcher matcher = regex.matcher(text);
        if (matcher.find()) {
            String[] groups = new String[matcher.groupCount() + 1];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i);
            }
            return new xin.yansh.course.searchengine.spider.selector.RegexResult(groups);
        }
        return xin.yansh.course.searchengine.spider.selector.RegexResult.EMPTY_RESULT;
    }

    public List<xin.yansh.course.searchengine.spider.selector.RegexResult> selectGroupList(String text) {
        Matcher matcher = regex.matcher(text);
        List<xin.yansh.course.searchengine.spider.selector.RegexResult> resultList = new ArrayList<xin.yansh.course.searchengine.spider.selector.RegexResult>();
        while (matcher.find()) {
            String[] groups = new String[matcher.groupCount() + 1];
            for (int i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i);
            }
            resultList.add(new xin.yansh.course.searchengine.spider.selector.RegexResult(groups));
        }
        return resultList;
    }

    @Override
    public String toString() {
        return regexStr;
    }

}
