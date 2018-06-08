package xin.yansh.course.searchengine;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

import static xin.yansh.course.searchengine.Config.PublicConfig;

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
            String contentType = (String) resultItems.getAll().get(PublicConfig.KEY_CONTENT_TYPE);
            String url = (String) resultItems.getAll().get(PublicConfig.KEY_URL);
            Document document = new Document();
            document.append(PublicConfig.KEY_URL, url);
            document.append(PublicConfig.KEY_CONTENT_TYPE, contentType);
            String charset = null;
            String html = null;
            List<String> links = null;
            byte[] bytes = null;
            switch (contentType) {
                case PublicConfig.HTML_CONTENT_TYPE: {
                    charset = (String) resultItems.getAll().get(PublicConfig.KEY_CHARSET);
                    document.append(PublicConfig.KEY_CHARSET, charset);
                    html = (String) resultItems.getAll().get(PublicConfig.KEY_HTML);
                    document.append(PublicConfig.KEY_HTML, html);
                    links = (List<String>) resultItems.getAll().get(PublicConfig.KEY_LINKS);
                    document.append(PublicConfig.KEY_LINKS, links);
                    break;
                }
                case PublicConfig.PDF_CONTENT_TYPE:
                case PublicConfig.WORD_CONTENT_TYPE: {
                    bytes = (byte[]) resultItems.getAll().get(PublicConfig.KEY_BYTES);
                    document.append(PublicConfig.KEY_BYTES, bytes);
                    document.append(PublicConfig.KEY_LINKS, emptyList);
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
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
