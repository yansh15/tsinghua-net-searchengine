package xin.yansh.course.searchengine;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import static xin.yansh.course.searchengine.Config.ContentType;
import static xin.yansh.course.searchengine.Config.PageProcessorConfig;
import static xin.yansh.course.searchengine.Config.PublicConfig;

public class MyPageProcessor implements PageProcessor {
    private Site site;
    private final Set<String> domains;
    private final Object pageCntMutex;
    private int pageCnt;

    private static int count(String s, char c) {
        int cnt = 0;
        for (int i = 0; i < s.length(); ++i)
            if (s.charAt(i) == c)
                ++cnt;
        return cnt;
    }

    public MyPageProcessor(Set<String> domains) {
        super();
        this.site = Site.me().setRetryTimes(3).setTimeOut(PublicConfig.THREADS * 1000).setSleepTime(PublicConfig.THREADS);
        this.domains = domains;
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
        if (contentTypeString.contains(PublicConfig.HTML_CONTENT_TYPE)) {
            contentType = ContentType.HTML;
        } else if (contentTypeString.contains(PublicConfig.PDF_CONTENT_TYPE)) {
            contentType = ContentType.PDF;
        } else if (contentTypeString.contains(PublicConfig.WORD_CONTENT_TYPE)) {
            contentType = ContentType.WORD;
        } else {
            page.setSkip(true);
            return;
        }
        String url = page.getRequest().getUrl();
        synchronized (pageCntMutex) {
            System.out.println(pageCnt + "\t\t" + url);
            ++pageCnt;
        }
        Matcher urlMatcher = PageProcessorConfig.PATTERN.matcher(url);
        if (!urlMatcher.matches()) {
            page.setSkip(true);
            return;
        }
        page.putField(PublicConfig.KEY_URL, urlMatcher.group(1));
        switch (contentType) {
            case HTML: {
                page.putField(PublicConfig.KEY_CONTENT_TYPE, PublicConfig.HTML_CONTENT_TYPE);
                page.putField(PublicConfig.KEY_CHARSET, page.getHtml().getDocument().charset().toString());
                page.putField(PublicConfig.KEY_HTML, page.getHtml().toString().replaceAll("\\s+", " "));
                List<String> targets = new ArrayList<>();
                for (String target : page.getHtml().links().all()) {
                    if (count(target, '?') > 1)
                        continue;
                    Matcher matcher = PageProcessorConfig.PATTERN.matcher(target);
                    if (!matcher.matches())
                        continue;
                    String domain = matcher.group(2);
                    synchronized (domains) {
                        if (domains.contains(domain))
                            targets.add(matcher.group(1));
                    }
                }
                page.putField(PublicConfig.KEY_LINKS, targets);
                page.addTargetRequests(targets);
                break;
            }
            case PDF: {
                page.putField(PublicConfig.KEY_CONTENT_TYPE, PublicConfig.PDF_CONTENT_TYPE);
                page.putField(PublicConfig.KEY_BYTES, page.getBytes());
                break;
            }
            case WORD: {
                page.putField(PublicConfig.KEY_CONTENT_TYPE, PublicConfig.WORD_CONTENT_TYPE);
                page.putField(PublicConfig.KEY_BYTES, page.getBytes());
                break;
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
