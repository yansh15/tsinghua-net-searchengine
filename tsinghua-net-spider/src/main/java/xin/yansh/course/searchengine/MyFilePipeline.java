package xin.yansh.course.searchengine;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import xin.yansh.course.searchengine.spider.ResultItems;
import xin.yansh.course.searchengine.spider.Task;
import xin.yansh.course.searchengine.spider.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

public class MyFilePipeline implements Pipeline {
    private final MongoCollection<Document> collection;
    private final List<String> emptyList;

    public MyFilePipeline(MongoCollection<Document> c) {
        super();
        collection = c;
        emptyList = new ArrayList<>();
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            String contentType = (String) resultItems.getAll().get(Config.KEY_CONTENT_TYPE);
            String url = (String) resultItems.getAll().get(Config.KEY_URL);
            Document document = new Document();
            document.append(Config.KEY_URL, url);
            document.append(Config.KEY_CONTENT_TYPE, contentType);
            String charset = null;
            String html = null;
            List<String> links = null;
            byte[] bytes = null;
            switch (contentType) {
                case Config.HTML_CONTENT_TYPE: {
                    charset = (String) resultItems.getAll().get(Config.KEY_CHARSET);
                    document.append(Config.KEY_CHARSET, charset);
                    html = (String) resultItems.getAll().get(Config.KEY_HTML);
                    document.append(Config.KEY_HTML, html);
                    links = (List<String>) resultItems.getAll().get(Config.KEY_LINKS);
                    document.append(Config.KEY_LINKS, links);
                    break;
                }
                case Config.PDF_CONTENT_TYPE:
                case Config.WORD_CONTENT_TYPE: {
                    bytes = (byte[]) resultItems.getAll().get(Config.KEY_BYTES);
                    document.append(Config.KEY_BYTES, bytes);
                    document.append(Config.KEY_LINKS, emptyList);
                    break;
                }
            }
            synchronized (collection) {
                collection.insertOne(document);
            }
            url = null;
            contentType = null;
            charset = null;
            html = null;
            for (String link : links)
                link = null;
            links = null;
            bytes = null;
            resultItems = null;
            task = null;
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
