package xin.yansh.course.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import static xin.yansh.course.searchengine.Config.FilePipelineConfig;
import static xin.yansh.course.searchengine.Config.PublicConfig;

public class MyFilePipeline implements Pipeline {
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public MyFilePipeline() {
        super();
        client = new MongoClient();
        database = client.getDatabase(FilePipelineConfig.DATABASE_NAME);
        collection = database.getCollection(FilePipelineConfig.COLLECTION_NAME);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            String contentType = (String) resultItems.getAll().get(PublicConfig.KEY_CONTENT_TYPE);
            String url = resultItems.getRequest().getUrl();
            Document document = new Document();
            document.append(PublicConfig.KEY_URL, url);
            document.append(PublicConfig.KEY_CONTENT_TYPE, contentType);
            switch (contentType) {
                case PublicConfig.HTML_CONTENT_TYPE: {
                    String charset = (String) resultItems.getAll().get(PublicConfig.KEY_CHARSET);
                    document.append(PublicConfig.KEY_CHARSET, charset);
                    String html = (String) resultItems.getAll().get(PublicConfig.KEY_HTML);
                    document.append(PublicConfig.KEY_HTML, html);
                    break;
                }
                case PublicConfig.PDF_CONTENT_TYPE:
                case PublicConfig.WORD_CONTENT_TYPE: {
                    byte[] bytes = (byte[]) resultItems.getAll().get(PublicConfig.KEY_BYTES);
                    document.append(PublicConfig.KEY_BYTES, bytes);
                    break;
                }
            }
            synchronized (collection) {
                collection.insertOne(document);
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
