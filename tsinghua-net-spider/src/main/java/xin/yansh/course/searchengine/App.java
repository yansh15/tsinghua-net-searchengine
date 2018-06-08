package xin.yansh.course.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.awt.print.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static xin.yansh.course.searchengine.Config.PublicConfig;

public class App {
    public static void main(String[] args) throws Exception {
        InputStream configInputStream = new FileInputStream(new File(PublicConfig.CONFIG_FILE));
        byte[] buffer = new byte[configInputStream.available()];
        configInputStream.read(buffer);
        JSONObject config = new JSONObject(new String(buffer, "UTF-8"));
        PublicConfig.IS_SMALL = config.getBoolean("is-small");
        PublicConfig.THREADS = config.getInt("threads");

        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(PublicConfig.DATABASE_NAME);

        List<String> seeds = new ArrayList<>();
        BufferedReader seedReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.getSeedFile()), "UTF-8"));
        for (String seed = seedReader.readLine(); seed != null && seed.length() != 0; seed = seedReader.readLine())
            seeds.add(seed);
        seedReader.close();

        Set<String> domains = new HashSet<>();
        BufferedReader domainReader = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(PublicConfig.getDomainFile()), "UTF-8"));
        for (String domain = domainReader.readLine(); domain != null && domain.length() != 0; domain = domainReader.readLine())
            domains.add(domain);
        domainReader.close();

        MongoCollection<Document> collection = database.getCollection(PublicConfig.getCollectionName());

        Spider spider = Spider.create(new MyPageProcessor(domains))
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000000, 0.01)))
                .addPipeline(new MyFilePipeline(collection))
                .thread(PublicConfig.THREADS);
        for (String seed : seeds)
            spider.addUrl(seed);
        spider.run();
    }
}
