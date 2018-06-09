package xin.yansh.course.searchengine;

import xin.yansh.course.searchengine.spider.Page;
import xin.yansh.course.searchengine.spider.Site;
import xin.yansh.course.searchengine.spider.processor.PageProcessor;
import xin.yansh.course.searchengine.spider.scheduler.QueueScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import static xin.yansh.course.searchengine.Config.ContentType;

public class MyPageProcessor implements PageProcessor {
    private Site site;
    private final Set<String> domains;
    private final QueueScheduler scheduler;
    private final Object pageCntMutex;
    private int pageCnt;

    private static int count(String s, char c) {
        int cnt = 0;
        for (int i = 0; i < s.length(); ++i)
            if (s.charAt(i) == c)
                ++cnt;
        return cnt;
    }

    public MyPageProcessor(Set<String> domains, QueueScheduler scheduler) {
        super();
        this.site = Site.me().setTimeOut(Config.THREADS * 1000).setSleepTime(Config.SLEEP_TIME);
        this.domains = domains;
        this.scheduler = scheduler;
        this.pageCntMutex = new Object();
        this.pageCnt = 0;
    }

    @Override
    public void process(Page page) {
        List<String> contentTypeList = page.getHeaders().get("Content-Type");
        if (contentTypeList.size() != 1) {
            page.setSkip(true);
            return;
        }
        String contentTypeString = contentTypeList.get(0);
        ContentType contentType;
        if (contentTypeString.contains(Config.HTML_CONTENT_TYPE)) {
            contentType = ContentType.HTML;
        } else if (contentTypeString.contains(Config.PDF_CONTENT_TYPE)) {
            contentType = ContentType.PDF;
        } else if (contentTypeString.contains(Config.WORD_CONTENT_TYPE)) {
            contentType = ContentType.WORD;
        } else {
            page.setSkip(true);
            return;
        }
        String url = page.getRequest().getUrl();
        synchronized (pageCntMutex) {
            System.out.println(scheduler.getTotalRequestsCount(null) + "\t" + scheduler.getLeftRequestsCount(null) + "\t" + pageCnt + "\t" + url);
            ++pageCnt;
        }
        Matcher urlMatcher = Config.PATTERN.matcher(url);
        if (!urlMatcher.matches()) {
            page.setSkip(true);
            return;
        }
        page.putField(Config.KEY_URL, urlMatcher.group(1));
        switch (contentType) {
            case HTML: {
                page.putField(Config.KEY_CONTENT_TYPE, Config.HTML_CONTENT_TYPE);
                page.putField(Config.KEY_CHARSET, page.getHtml().getDocument().charset().toString());
                page.putField(Config.KEY_HTML, page.getHtml().toString().replaceAll("\\s+", " "));
                List<String> targets = new ArrayList<>();
                for (String target : page.getHtml().links().all()) {
                    if (count(target, '?') > 1)
                        continue;
                    Matcher matcher = Config.PATTERN.matcher(target);
                    if (!matcher.matches())
                        continue;
                    String domain = matcher.group(2);
                    synchronized (domains) {
                        if (domains.contains(domain))
                            targets.add(matcher.group(1));
                    }
                }
                page.putField(Config.KEY_LINKS, targets);
                page.addTargetUrls(targets);
                break;
            }
            case PDF: {
                page.putField(Config.KEY_CONTENT_TYPE, Config.PDF_CONTENT_TYPE);
                page.putField(Config.KEY_BYTES, page.getBytes());
                break;
            }
            case WORD: {
                page.putField(Config.KEY_CONTENT_TYPE, Config.WORD_CONTENT_TYPE);
                page.putField(Config.KEY_BYTES, page.getBytes());
                break;
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
