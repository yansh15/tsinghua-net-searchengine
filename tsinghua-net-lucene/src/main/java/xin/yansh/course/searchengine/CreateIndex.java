package xin.yansh.course.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static xin.yansh.course.searchengine.Config.MongoDBConfig;

public class CreateIndex {
    public static void main(String[] args) throws Exception {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(MongoDBConfig.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(Config.getCollectionName());

        PageIndexer indexer = new PageIndexer();
        indexer.indexMongoDBCollection(collection);
        indexer.close();
        client.close();
    }
}
