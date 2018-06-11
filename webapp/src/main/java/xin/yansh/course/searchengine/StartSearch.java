package xin.yansh.course.searchengine;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hankcs.lucene.HanLPAnalyzer;

import static xin.yansh.course.searchengine.Config.LuceneConfig;
import static xin.yansh.course.searchengine.Config.SearchFieldWeightConfig;
import static xin.yansh.course.searchengine.Config.PageConfig;
import static xin.yansh.course.searchengine.Config.MongoDBConfig;

public class StartSearch {
	private Analyzer analyzer;
	private IndexSearcher searcher;
	private IndexReader reader;
	private Map<String, Float> weight;
	private String[] AllField;
	
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
		AllField = new String[] {LuceneConfig.FIELD_CONTENT, LuceneConfig.FIELD_TITLE, LuceneConfig.FIELD_KEYWORD, LuceneConfig.FIELD_URL, LuceneConfig.FIELD_H};
	}
	
	public TopDocs searchQuery(String queryString, int page, boolean pdf, boolean word, boolean html, String[] shields, String url_limit) throws Exception {
		QueryParser parser = new MultiFieldQueryParser(AllField, analyzer, weight);
		Query parsed_query = parser.parse(queryString);
		Query pdf_query = new TermQuery(new Term(LuceneConfig.FIELD_CONTENT_TYPE, MongoDBConfig.PDF_CONTENT_TYPE));
		Query word_query = new TermQuery(new Term(LuceneConfig.FIELD_CONTENT_TYPE, MongoDBConfig.WORD_CONTENT_TYPE));
		Query html_query = new TermQuery(new Term(LuceneConfig.FIELD_CONTENT_TYPE, MongoDBConfig.HTML_CONTENT_TYPE));
		BooleanClause bc1 = new BooleanClause(pdf_query, pdf == true ? Occur.SHOULD : Occur.MUST_NOT);
		BooleanClause bc2 = new BooleanClause(word_query, word == true ? Occur.SHOULD : Occur.MUST_NOT);
		BooleanClause bc3 = new BooleanClause(html_query, html == true ? Occur.SHOULD : Occur.MUST_NOT);
		BooleanClause bc4 = new BooleanClause(parsed_query, Occur.MUST);
		BooleanQuery.Builder builder = new BooleanQuery.Builder().add(bc1).add(bc2).add(bc3).add(bc4);
		for (String shield : shields) {
			Query shield_query = parser.parse(shield);
			BooleanClause bc = new BooleanClause(shield_query, Occur.MUST_NOT);
			builder.add(bc);
		}
		Query booleanQuery = builder.build();
		//System.out.println(booleanQuery.toString());
		Query my_query = new MyScoreQuery(booleanQuery, url_limit);
		TopDocs topDocs;
		if (page == 1)
			topDocs = searcher.search(my_query, PageConfig.PAGE_SIZE);
		else {
			topDocs = searcher.search(my_query, PageConfig.PAGE_SIZE * (page - 1));
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
				System.out.println(doc + "[" + document.get(LuceneConfig.FIELD_URL) + "]" + " pagerank=" + document.get(LuceneConfig.FIELD_PAGERANK));
			}
		}
		return topDocs;
	}
	
	public TopDocs searchQuery(String queryString, int page) throws Exception {
		return searchQuery(queryString, page, true, false, false, new String[] {}, null);
	}
	
	public String getUrl(ScoreDoc doc) throws Exception {
		Document document = searcher.doc(doc.doc);
		return document.get(LuceneConfig.FIELD_URL);
	}

	public String getTitle(ScoreDoc doc) throws Exception {
		Document document = searcher.doc(doc.doc);
		return document.get(LuceneConfig.FIELD_TITLE);
	}

	public String getContentType(ScoreDoc doc) throws Exception {
		Document document = searcher.doc(doc.doc);
		return document.get(LuceneConfig.FIELD_CONTENT_TYPE);
	}

	public String getContent(ScoreDoc doc) throws Exception {
		Document document = searcher.doc(doc.doc);
		return document.get(LuceneConfig.FIELD_CONTENT);
	}
	
	private void close() throws Exception {
		analyzer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws Exception {
		StartSearch startSearch = new StartSearch();
		startSearch.searchQuery("计算", 0, true, false, false, new String[0], "");
		startSearch.close();
	}
}
