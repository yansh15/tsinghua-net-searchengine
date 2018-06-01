package xin.yansh.course.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static xin.yansh.course.searchengine.Config.PublicConfig;

public class App {
    public static void main(String[] args) throws IOException {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(PublicConfig.DATABASE_NAME);

        if (!PublicConfig.IS_SMALL) {
            List<String> seeds = new ArrayList<>();
            BufferedReader seedReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.SEED_FILE), "UTF-8"));
            for (String seed = seedReader.readLine(); seed != null && seed.length() != 0; seed = seedReader.readLine())
                seeds.add(seed);
            seedReader.close();

            Set<String> domains = new HashSet<>();
            BufferedReader domainReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.DOMAIN_FILE), "UTF-8"));
            for (String domain = domainReader.readLine(); domain != null && domain.length() != 0; domain = domainReader.readLine())
                domains.add(domain);
            domainReader.close();

            MongoCollection<Document> collection = database.getCollection(PublicConfig.COLLECTION_NAME);

            Spider spider = Spider.create(new MyPageProcessor(domains))
                    .setScheduler(new QueueScheduler())
                    .addPipeline(new MyFilePipeline(collection))
                    .thread(1000);
            for (String seed : seeds)
                spider.addUrl(seed);
            spider.run();
        } else {
            List<String> smallSeeds = new ArrayList<>();
            BufferedReader smallSeedReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.SMALL_SEED_FILE), "UTF-8"));
            for (String seed = smallSeedReader.readLine(); seed != null && seed.length() != 0; seed = smallSeedReader.readLine())
                smallSeeds.add(seed);
            smallSeedReader.close();

            Set<String> smallDomains = new HashSet<>();
            BufferedReader smallDomainReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.SMALL_DOMAIN_FILE), "UTF-8"));
            for (String domain = smallDomainReader.readLine(); domain != null && domain.length() != 0; domain = smallDomainReader.readLine())
                smallDomains.add(domain);
            smallDomainReader.close();

            MongoCollection<Document> smallCollection = database.getCollection(PublicConfig.SMALL_COLLECTION_NAME);

            Spider smallSpider = Spider.create(new MyPageProcessor(smallDomains))
                    .setScheduler(new QueueScheduler())
                    .addPipeline(new MyFilePipeline(smallCollection))
                    .thread(100);
            for (String seed : smallSeeds)
                smallSpider.addUrl(seed);
            smallSpider.run();
        }
    }
}
