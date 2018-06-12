package xin.yansh.course.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;

import static xin.yansh.course.searchengine.Config.MongoDBConfig;

class PageInfo implements Comparable<PageInfo> {
    String url;
    int outDegree;
    double pageRank;
    double nextPageRank;

    PageInfo(String u) {
        url = u;
        outDegree = 0;
        pageRank = 0.0;
        nextPageRank = 0.0;
    }

    @Override
    public int compareTo(PageInfo o) {
        if (pageRank > o.pageRank)
            return 1;
        if (pageRank == o.pageRank)
            return 0;
        return -1;
    }
}

public class CalcPageRank {
    private static final double ALPHA = 0.15;

    private static int cnt = 0;

    public static void main(String[] args) {
        HashMap<String, PageInfo> pages = new HashMap<>();

        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(MongoDBConfig.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(Config.getCollectionName());

        cnt = 0;
        for (Document document : collection.find()) {
            String url = document.getString(MongoDBConfig.KEY_URL);
            pages.put(url, new PageInfo(url));
            if (cnt % 100 == 0)
                System.out.printf("Init: %6d\n", cnt);
            ++cnt;
        }

        cnt = 0;
        for (Document document : collection.find()) {
            String url = document.getString(MongoDBConfig.KEY_URL);
            PageInfo info = pages.get(url);
            List<String> links = (List<String>) document.get(MongoDBConfig.KEY_LINKS);
            for (String link : links)
                if (pages.containsKey(link))
                    ++info.outDegree;
            if (cnt % 100 == 0)
                System.out.printf("Calc Out-Degree: %6d\n", cnt);
            ++cnt;
        }

        double S = 0.0;
        double nextS = 0.0;

        cnt = 0;
        for (PageInfo info : pages.values()) {
            info.pageRank = 1.0 / pages.size();
            info.nextPageRank = ALPHA / pages.size();
            if (info.outDegree == 0)
                S = S + info.pageRank;
            if (cnt % 100 == 0)
                System.out.printf("Init PageRank: %6d\n", cnt);
            ++cnt;
        }

        for (int round = 0; round < 30; ++round) {
            System.out.printf("Round %2d\n", round);
            double max = 0.0;
            cnt = 0;
            for (Document document : collection.find()) {
                String url = document.getString(MongoDBConfig.KEY_URL);
                PageInfo info = pages.get(url);
                List<String> links = (List<String>) document.get(MongoDBConfig.KEY_LINKS);
                for (String link : links)
                    if (pages.containsKey(link)) {
                        PageInfo outInfo = pages.get(link);
                        outInfo.nextPageRank = outInfo.nextPageRank + (1 - ALPHA) * info.pageRank / info.outDegree;
                    }
                if (cnt % 100 == 0)
                    System.out.printf("Round: %2d, Calc: %6d\n", round, cnt);
                ++cnt;
            }
            nextS = 0.0;
            cnt = 0;
            for (PageInfo info : pages.values()) {
                info.nextPageRank = info.nextPageRank + (1 - ALPHA) * S / pages.size();
                max = Math.max(max, Math.abs(info.pageRank - info.nextPageRank) / info.pageRank);
                info.pageRank = info.nextPageRank;
                info.nextPageRank = ALPHA / pages.size();
                if (info.outDegree == 0)
                    nextS = nextS + info.pageRank;
                if (cnt % 100 == 0)
                    System.out.printf("Round: %2d, Update: %6d\n", round, cnt);
                ++cnt;
            }
            System.out.printf("Max Relative Rrror: %.6E\n", max);
            S = nextS;
        }

        cnt = 0;
        for (PageInfo info : pages.values()) {
            collection.updateOne(Filters.eq(MongoDBConfig.KEY_URL, info.url), new Document("$set", new Document(MongoDBConfig.KEY_PAGERANK, info.pageRank)));
            if (cnt % 100 == 0)
                System.out.printf("Complete: %6d\n", cnt);
            ++cnt;
        }

        client.close();
    }
}
