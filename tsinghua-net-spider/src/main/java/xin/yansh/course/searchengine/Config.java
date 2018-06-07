package xin.yansh.course.searchengine;

import java.util.regex.Pattern;

class Config {
    static class PublicConfig {
        static boolean IS_SMALL = false;
        static int THREADS = 10;

        static final String CONFIG_FILE = "config.json";
        static final String SEED_FILE = "/seed.txt";
        static final String SMALL_SEED_FILE = "/small_seed.txt";
        static final String DOMAIN_FILE = "/domain.txt";
        static final String SMALL_DOMAIN_FILE = "/small_domain.txt";

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

        static String getSeedFile() {
            return IS_SMALL ? SMALL_SEED_FILE : SEED_FILE;
        }

        static String getDomainFile() {
            return IS_SMALL ? SMALL_DOMAIN_FILE : DOMAIN_FILE;
        }

        static String getCollectionName() {
            return IS_SMALL ? SMALL_COLLECTION_NAME : COLLECTION_NAME;
        }
    }

    static class PageProcessorConfig {
        static final Pattern PATTERN = Pattern.compile("(((http|https)://([A-Za-z0-9]+\\.)+tsinghua\\.edu\\.cn)(/[^#]*)?)(#.+)?");
    }

    enum ContentType {
        HTML,
        PDF,
        WORD
    }
}
