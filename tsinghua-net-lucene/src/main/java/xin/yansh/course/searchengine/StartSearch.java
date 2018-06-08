package xin.yansh.course.searchengine;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hankcs.lucene.HanLPAnalyzer;

import static xin.yansh.course.searchengine.Config.LuceneConfig;
import static xin.yansh.course.searchengine.Config.SearchFieldWeightConfig;
import static xin.yansh.course.searchengine.Config.PageConfig;

public class StartSearch {
	private Analyzer analyzer;
	private IndexSearcher searcher;
	private IndexReader reader;
	private Map<String, Float> weight;
	
	public StartSearch() throws Exception {
		Directory directory = FSDirectory.open(Paths.get(Config.getIndexDirectory()));
		analyzer = new HanLPAnalyzer();
		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
		weight = new HashMap<String, Float>();
		weight.put(LuceneConfig.FIELD_CONTENT, SearchFieldWeightConfig.CONTENT_WEIGHT);
		weight.put(LuceneConfig.FIELD_TITLE, SearchFieldWeightConfig.TITLE_WEIGHT);
		weight.put(LuceneConfig.FIELD_KEYWORD, SearchFieldWeightConfig.KEYWORD_WEIGHT);
		weight.put(LuceneConfig.FIELD_H, SearchFieldWeightConfig.H_WEIGHT);
		weight.put(LuceneConfig.FIELD_URL, SearchFieldWeightConfig.URL_WEIGHT);
	}
	
	public TopDocs searchQuery(String queryString, int page) throws Exception {
		QueryParser parser = new MultiFieldQueryParser(new String[] {LuceneConfig.FIELD_CONTENT, LuceneConfig.FIELD_TITLE, LuceneConfig.FIELD_KEYWORD, LuceneConfig.FIELD_URL, LuceneConfig.FIELD_H}, analyzer, weight);
		Query my_query = parser.parse(queryString);
		TopDocs topDocs;
		if (page == 0)
			topDocs = searcher.search(my_query, PageConfig.PAGE_SIZE);
		else {
			topDocs = searcher.search(my_query, PageConfig.PAGE_SIZE * page);
			topDocs = searcher.searchAfter(topDocs.scoreDocs[topDocs.scoreDocs.length - 1], my_query, PageConfig.PAGE_SIZE);
		}
		if (topDocs.totalHits == 0)
			System.out.println("No Hits");
		else {
			System.out.println(topDocs.totalHits);
			ScoreDoc[] docs = topDocs.scoreDocs;
			for (int i = 0; i < docs.length; ++i) {
				ScoreDoc doc = docs[i];
				Document document = searcher.doc(doc.doc);
				System.out.println(doc + "[" + document.get("url") + "]");
			}
		}
		return topDocs;
	}
	
	public String getUrl(ScoreDoc doc) throws Exception {
		Document document = searcher.doc(doc.doc);
		return document.get("url");
	}
	
	private void close() throws Exception {
		analyzer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws Exception {
		StartSearch startSearch = new StartSearch();
		startSearch.searchQuery("秦腔", 0);
		startSearch.searchQuery("秦腔", 1);
		startSearch.close();
	}
}
