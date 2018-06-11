package xin.yansh.course.searchengine;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.alibaba.fastjson.JSONObject;
import xin.yansh.course.searchengine.spider.Spider;
import xin.yansh.course.searchengine.spider.scheduler.component.BloomFilterDuplicateRemover;
import xin.yansh.course.searchengine.spider.scheduler.QueueScheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StartSpider {
    public static void main(String[] args) throws Exception {
        InputStream configInputStream = new FileInputStream(new File(Config.CONFIG_FILE));
        byte[] buffer = new byte[configInputStream.available()];
        configInputStream.read(buffer);
        JSONObject config = JSON.parseObject(new String(buffer, "UTF-8"));
        Config.IS_SMALL = config.getBoolean(Config.JSON_IS_SMALL);
        Config.THREADS = config.getIntValue(Config.JSON_THREADS);
        Config.SLEEP_TIME = config.getIntValue(Config.JSON_SLEEP_TIME);

        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase(Config.DATABASE_NAME);

        List<String> seeds = new ArrayList<>();
        for (Object object : config.getJSONArray(Config.getSeedKey()))
            seeds.add((String) object);

        Set<String> domains = new HashSet<>();
        for (Object object : config.getJSONArray(Config.getDomainKey()))
            domains.add((String) object);

        MongoCollection<Document> collection = database.getCollection(Config.getCollectionName());
        collection.drop();

        QueueScheduler scheduler = (QueueScheduler) new QueueScheduler();

        Spider.create(new MyPageProcessor(domains, scheduler))
                .setScheduler(scheduler)
                .addPipeline(new MyFilePipeline(collection))
                .startUrls(seeds)
                .thread(Config.THREADS)
                .run();
    }
}
