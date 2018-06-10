package xin.yansh.course.searchengine;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

import static xin.yansh.course.searchengine.Config.LuceneConfig;
import static xin.yansh.course.searchengine.Config.ScoreConfig;
import static xin.yansh.course.searchengine.Config.MongoDBConfig;

public class MyScoreQuery extends CustomScoreQuery {
	private String url_limit;
	
	public MyScoreQuery(Query sunquery, String url) {
		super(sunquery);
		this.url_limit = url;
	}
	
	private class MyScoreProvider extends CustomScoreProvider {
		private LeafReader reader;
		private Set<String> fieldsToLoad;
		private String provide_url_limit;
		
		public MyScoreProvider(LeafReaderContext context, String provide_url) {
			super(context);
			provide_url_limit = provide_url;
			reader = context.reader();
			fieldsToLoad = new HashSet<>();
			fieldsToLoad.add(LuceneConfig.FIELD_PAGERANK);
			fieldsToLoad.add(LuceneConfig.FIELD_CONTENT_TYPE);
			fieldsToLoad.add(LuceneConfig.FIELD_URL);
		}
		
		@Override
		public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
			Document document = reader.document(doc, fieldsToLoad);
			IndexableField field = document.getField(LuceneConfig.FIELD_PAGERANK);
			IndexableField url_field = document.getField(LuceneConfig.FIELD_URL);
			float influence;
			if (ScoreConfig.WITH_PAGERANK)
				influence = field.numericValue().floatValue() * ScoreConfig.COEF_PAGERANK + ScoreConfig.BIAS_PAGERANK;
			else
				influence = 1.0f;
			if (provide_url_limit != null && url_field.stringValue().contains(provide_url_limit))
				influence = 0.0f;
			return subQueryScore * influence;
		}
	}
	
	@Override
	protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
		return new MyScoreProvider(context, url_limit);
	}
}
