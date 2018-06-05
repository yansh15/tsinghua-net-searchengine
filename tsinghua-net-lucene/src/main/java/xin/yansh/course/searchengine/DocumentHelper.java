package xin.yansh.course.searchengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.List;

public class DocumentHelper {
    public static String getTextFromTag(Element element, String tag) {
        StringBuilder builder = new StringBuilder();
        for (Element te : element.select(tag))
            builder.append(Jsoup.clean(te.html(), Whitelist.none())).append(' ');
        return builder.toString().replaceAll("\\s+", " ");
    }

    public static String getTextFromTags(Element element, List<String> tags) {
        StringBuilder builder = new StringBuilder();
        for (String tag : tags)
            builder.append(getTextFromTag(element, tag));
        return builder.toString();
    }

    public static String getTextFromTag(Elements elements, String tag) {
        StringBuilder builder = new StringBuilder();
        for (Element element : elements)
            builder.append(getTextFromTag(element, tag));
        return builder.toString();
    }

    public static String getTextFromTags(Elements elements, List<String> tags) {
        StringBuilder builder = new StringBuilder();
        for (Element element : elements)
            builder.append(getTextFromTags(element, tags));
        return builder.toString();
    }

    public static String getText(Element element) {
        return Jsoup.clean(element.html(), Whitelist.none()).replaceAll("\\s+", " ");
    }

    public static String getText(Elements elements) {
        StringBuilder builder = new StringBuilder();
        for (Element element : elements)
            builder.append(getText(element)).append('\n');
        return builder.toString().replaceAll("\\s+", " ");
    }
}
