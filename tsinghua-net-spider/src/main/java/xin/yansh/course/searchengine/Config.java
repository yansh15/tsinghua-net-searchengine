package xin.yansh.course.searchengine;

import java.util.regex.Pattern;

class Config {
    static class PublicConfig {
        static final String HTML_CONTENT_TYPE = "text/html";
        static final String PDF_CONTENT_TYPE = "application/pdf";
        static final String WORD_CONTENT_TYPE = "application/msword";

        static final String KEY_CONTENT_TYPE = "content-type";
        static final String KEY_BYTES = "bytes";
        static final String KEY_CHARSET = "charset";
        static final String KEY_HTML = "html";

        static final String KEY_URL = "url";
    }

    static class PageProcessorConfig {
        static final Pattern PATTERN = Pattern.compile("(((http|https)://([A-Za-z0-9]+\\.)+tsinghua\\.edu\\.cn)(/[^#]*)?)(#.+)?");
    }

    static class FilePipelineConfig {
        static final String DATABASE_NAME = "tsinghua-net";
        static final String COLLECTION_NAME = "page";
    }

    enum ContentType {
        HTML,
        PDF,
        WORD
    }
}
