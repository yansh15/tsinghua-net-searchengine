package xin.yansh.course.searchengine;

import com.hankcs.hanlp.HanLP;
import com.hankcs.lucene.HanLPIndexAnalyzer;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bson.Document;
import org.bson.types.Binary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xin.yansh.course.searchengine.Config.MongoDBConfig;

public class CreateIndex {
    private Analyzer analyzer;
    private IndexWriter indexWriter;

    public CreateIndex() throws Exception {
        Directory directory = FSDirectory.open(Paths.get(Config.getIndexDirectory()));
        analyzer = new HanLPIndexAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setCommitOnClose(true);
        config.setSimilarity(new BM25Similarity(Config.BM25Config.K1, Config.BM25Config.B));
        indexWriter = new IndexWriter(directory, config);
    }

    public void indexMongoDBCollection(MongoCollection<Document> collection) throws Exception {
        FindIterable<Document> iter = collection.find();
        MongoCursor<Document> cursor = iter.iterator();
        for (int i = 0; cursor.hasNext(); ++i) {
            System.out.println(i);
            indexMongoDBDocument(cursor.next());

        }
    }

    private void indexMongoDBDocument(Document document) throws Exception {
        String contentType = (String) document.get(MongoDBConfig.KEY_CONTENT_TYPE);
        String url = (String) document.get(MongoDBConfig.KEY_URL);
        double pageRank = (Double) document.get(MongoDBConfig.KEY_PAGERANK);
        switch (contentType) {
            case MongoDBConfig.HTML_CONTENT_TYPE: {
                String charset = (String) document.get(MongoDBConfig.KEY_CHARSET);
                String html = (String) document.get(MongoDBConfig.KEY_HTML);
                indexHtml(contentType, url, charset, html, pageRank);
                break;
            }
            case MongoDBConfig.PDF_CONTENT_TYPE: {
                byte[] bytes = ((Binary) document.get(MongoDBConfig.KEY_BYTES)).getData();
                indexPDF(contentType, url, bytes, pageRank);
                break;
            }
            case MongoDBConfig.WORD_CONTENT_TYPE: {
                byte[] bytes = ((Binary) document.get(MongoDBConfig.KEY_BYTES)).getData();
                indexWord(contentType, url, bytes, pageRank);
                break;
            }
        }
    }

    private void indexHtml(String contentType, String url, String charset, String html, double pageRank) {
        try {
            List<Field> fieldList = new ArrayList<>();

            Field contentTypeField = new StringField(Config.LuceneConfig.FIELD_CONTENT_TYPE, contentType, Field.Store.YES);
            fieldList.add(contentTypeField);

            Field urlField = new StringField(Config.LuceneConfig.FIELD_URL, url, Field.Store.YES);
            fieldList.add(urlField);

            Field charsetField = new StoredField(Config.LuceneConfig.FIELD_CHARSET, charset);
            fieldList.add(charsetField);

            org.jsoup.nodes.Document document = Jsoup.parse(html);

            Element titleElement = document.select("title").first();
            String title = titleElement != null ? titleElement.text() : "";
            Field titleField = new TextField(Config.LuceneConfig.FIELD_TITLE, title, Field.Store.YES);
            fieldList.add(titleField);

            Elements mainElements = document.select("article");
            if (mainElements.size() == 0)
                mainElements.add(document.body());

            String h = DocumentHelper.getTextFromTags(mainElements, Config.LuceneConfig.H_TAG_LIST);
            Field hField = new TextField(Config.LuceneConfig.FIELD_H, h, Field.Store.YES);
            fieldList.add(hField);

            String content = DocumentHelper.getText(mainElements);
            Field contentField = new TextField(Config.LuceneConfig.FIELD_CONTENT, content, Field.Store.YES);
            fieldList.add(contentField);

            Element keywordElement = document.select("meta[name=keywords]").first();
            String keyword = keywordElement != null ? keywordElement.attr("content").trim() : null;
            if (keyword == null) {
                StringBuilder builder = new StringBuilder();
                for (String s : HanLP.extractKeyword(content, 3))
                    builder.append(s).append(' ');
                keyword = builder.toString();
            }
            Field keywordField = new TextField(Config.LuceneConfig.FIELD_KEYWORD, keyword, Field.Store.YES);
            fieldList.add(keywordField);

            Field pageRankField = new StoredField(Config.LuceneConfig.FIELD_PAGERANK, pageRank);
            fieldList.add(pageRankField);

            indexWriter.addDocument(fieldList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indexPDF(String contentType, String url, byte[] bytes, double pageRank) {
        try {
            List<Field> fieldList = new ArrayList<>();

            Field contentTypeField = new StringField(Config.LuceneConfig.FIELD_CONTENT_TYPE, contentType, Field.Store.YES);
            fieldList.add(contentTypeField);

            Field urlField = new StringField(Config.LuceneConfig.FIELD_URL, url, Field.Store.YES);
            fieldList.add(urlField);

            String title = new File(url).getName();
            Field titleField = new TextField(Config.LuceneConfig.FIELD_TITLE, title, Field.Store.YES);
            fieldList.add(titleField);

            PDDocument document = PDDocument.load(bytes);
            String content = new PDFTextStripper().getText(document);
            document.close();
            Field contentField = new TextField(Config.LuceneConfig.FIELD_CONTENT, content, Field.Store.YES);
            fieldList.add(contentField);

            StringBuilder builder = new StringBuilder();
            for (String s : HanLP.extractKeyword(content, 3))
                builder.append(s).append(' ');
            String keyword = builder.toString();
            Field keywordField = new TextField(Config.LuceneConfig.FIELD_KEYWORD, keyword, Field.Store.YES);
            fieldList.add(keywordField);

            Field pageRankField = new StoredField(Config.LuceneConfig.FIELD_PAGERANK, pageRank);
            fieldList.add(pageRankField);

            indexWriter.addDocument(fieldList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indexWord(String contentType, String url, byte[] bytes, double pageRank) {
        try {
            List<Field> fieldList = new ArrayList<>();

            Field contentTypeField = new StringField(Config.LuceneConfig.FIELD_CONTENT_TYPE, contentType, Field.Store.YES);
            fieldList.add(contentTypeField);

            Field urlField = new StringField(Config.LuceneConfig.FIELD_URL, url, Field.Store.YES);
            fieldList.add(urlField);

            String title = new File(url).getName();
            Field titleField = new TextField(Config.LuceneConfig.FIELD_TITLE, title, Field.Store.YES);
            fieldList.add(titleField);

            String typeName = title.substring(title.lastIndexOf('.') + 1);
            String content;
            switch (typeName) {
                case "doc": {
                    HWPFDocument document = new HWPFDocument(new ByteArrayInputStream(bytes));
                    content = document.getDocumentText().replaceAll("\\s+", " ");
                    document.close();
                    break;
                }
                case "docx": {
                    POIXMLTextExtractor extractor = new XWPFWordExtractor(new XWPFDocument(new ByteArrayInputStream(bytes)));
                    content = extractor.getText().replaceAll("\\s+", " ");
                    extractor.close();
                    break;
                }
                default: {
                    content = "";
                }
            }
            Field contentField = new TextField(Config.LuceneConfig.FIELD_CONTENT, content, Field.Store.YES);
            fieldList.add(contentField);

            StringBuilder builder = new StringBuilder();
            for (String s : HanLP.extractKeyword(content, 3))
                builder.append(s).append(' ');
            String keyword = builder.toString();
            Field keywordField = new TextField(Config.LuceneConfig.FIELD_KEYWORD, keyword, Field.Store.YES);
            fieldList.add(keywordField);

            Field pageRankField = new StoredField(Config.LuceneConfig.FIELD_PAGERANK, pageRank);
            fieldList.add(pageRankField);

            indexWriter.addDocument(fieldList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception {
        indexWriter.commit();
        indexWriter.close();
    }

    public static void main(String[] args) throws Exception {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(MongoDBConfig.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(Config.getCollectionName());

        CreateIndex indexer = new CreateIndex();
        indexer.indexMongoDBCollection(collection);
        indexer.close();
        client.close();
    }
}
