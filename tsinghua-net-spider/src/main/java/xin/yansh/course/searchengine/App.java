package xin.yansh.course.searchengine;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App {
    private static final String seedFile = "/seed.txt";
    private static final String domainFile = "/domain.txt";

    public static void main(String[] args) throws IOException {
        List<String> seeds = new ArrayList<>();
        BufferedReader seedReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(seedFile), "UTF-8"));
        for (String seed = seedReader.readLine(); seed != null && seed.length() != 0; seed = seedReader.readLine())
            seeds.add(seed);
        seedReader.close();

        Set<String> domains = new HashSet<>();
        BufferedReader domainReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(domainFile), "UTF-8"));
        for (String domain = domainReader.readLine(); domain != null && domain.length() != 0; domain = domainReader.readLine())
            domains.add(domain);
        domainReader.close();

        Spider spider = Spider.create(new MyPageProcessor(domains))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000000)))
                .addPipeline(new MyFilePipeline())
                .thread(1000);
        for (String seed : seeds)
            spider.addUrl(seed);
        spider.run();
    }
}
