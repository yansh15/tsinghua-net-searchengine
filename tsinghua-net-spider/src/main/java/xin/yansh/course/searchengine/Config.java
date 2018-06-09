package xin.yansh.course.searchengine;

import java.util.regex.Pattern;

class Config {
    static boolean IS_SMALL = false;
    static int THREADS = 10;
    static int SLEEP_TIME = 10;

    static final String CONFIG_FILE = "config.json";
    static final String JSON_IS_SMALL = "is-small";
    static final String JSON_THREADS = "threads";
    static final String JSON_SLEEP_TIME = "sleep-time";
    static final String JSON_SEED = "seed";
    static final String JSON_SMALL_SEED = "small-seed";
    static final String JSON_DOMAIN = "domain";
    static final String JSON_SMALL_DOMAIN = "small-domain";

    static final String HTML_CONTENT_TYPE = "text/html";
    static final String PDF_CONTENT_TYPE = "application/pdf";
    static final String WORD_CONTENT_TYPE = "application/msword";

    static final String KEY_CONTENT_TYPE = "content_type";
    static final String KEY_BYTES = "bytes";
    static final String KEY_CHARSET = "charset";
    static final String KEY_HTML = "html";
    static final String KEY_URL = "url";
    static final String KEY_LINKS = "links";

    static final String DATABASE_NAME = "tsinghua_net";
    static final String COLLECTION_NAME = "page";
    static final String SMALL_COLLECTION_NAME = "small_page";

    static final Pattern PATTERN = Pattern.compile("(((http|https)://([A-Za-z0-9]+\\.)+tsinghua\\.edu\\.cn)(/[^#]*)?)(#.+)?");

    static String getSeedKey() {
        return IS_SMALL ? JSON_SMALL_SEED : JSON_SEED;
    }

    static String getDomainKey() {
        return IS_SMALL ? JSON_SMALL_DOMAIN : JSON_DOMAIN;
    }

    static String getCollectionName() {
        return IS_SMALL ? SMALL_COLLECTION_NAME : COLLECTION_NAME;
    }

    enum ContentType {
        HTML,
        PDF,
        WORD
    }
}
