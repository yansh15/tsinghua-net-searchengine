package xin.yansh.course.searchengine;

import java.util.ArrayList;
import java.util.List;

class Config {
    static class PublicConfig {
        static final Boolean IS_SMALL = false;
    }
    
    static class ScoreConfig {
    	/*
    	 * Score = BM25_Score * (Coefficient * PageRank_Score + Bias) (if With_PageRank else BM25_Score);
    	 */
    	static final boolean WITH_PAGERANK = true;
    	static final float COEF_PAGERANK = 40f;
    	static final float BIAS_PAGERANK = 1f;
    }
    
    static class LuceneConfig {
        static final String INDEX_DIRECTORY = "../PageIndex";
        static final String SMALL_INDEX_DIRECTORY = "../SmallPageIndex";

        static final String FIELD_CONTENT_TYPE = "content_type";
        static final String FIELD_URL = "url";
        static final String FIELD_CHARSET = "charset";
        static final String FIELD_TITLE = "title";
        static final String FIELD_KEYWORD = "keyword";
        static final String FIELD_H = "h";
        static final String FIELD_CONTENT = "content";
        static final String FIELD_PAGERANK = "pagerank";

        static final List<String> H_TAG_LIST = new ArrayList<String>() {{
            add("h1"); add("h2"); add("h3"); add("h4"); add("h5"); add("h6");
        }};
    }

    static class BM25Config {
        static final float K1 = 1.2f;
        static final float B = 0.75f;
    }

    static class MongoDBConfig {
        static final String HTML_CONTENT_TYPE = "text/html";
        static final String PDF_CONTENT_TYPE = "application/pdf";
        static final String WORD_CONTENT_TYPE = "application/msword";

        static final String KEY_CONTENT_TYPE = "content_type";
        static final String KEY_BYTES = "bytes";
        static final String KEY_CHARSET = "charset";
        static final String KEY_HTML = "html";
        static final String KEY_URL = "url";
        static final String KEY_LINKS = "links";
        static final String KEY_PAGERANK = "pagerank";

        static final String DATABASE_NAME = "tsinghua_net";
        static final String COLLECTION_NAME = "page";
        static final String SMALL_COLLECTION_NAME = "small_page";
    }
    
    static class SearchFieldWeightConfig {
    	static final float TITLE_WEIGHT = 0.4f;
    	static final float CONTENT_WEIGHT = 0.04f;
    	static final float H_WEIGHT = 0.16f;
    	static final float URL_WEIGHT = 1.0f;
    	static final float KEYWORD_WEIGHT = 0.4f;
    }
    
    static class PageConfig {
    	static final int PAGE_SIZE = 10;
    }

    static String getIndexDirectory() {
        return PublicConfig.IS_SMALL ? LuceneConfig.SMALL_INDEX_DIRECTORY : LuceneConfig.INDEX_DIRECTORY;
    }

    static String getCollectionName() {
        return PublicConfig.IS_SMALL ? MongoDBConfig.SMALL_COLLECTION_NAME : MongoDBConfig.COLLECTION_NAME;
    }
}
