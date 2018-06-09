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

public class MyScoreQuery extends CustomScoreQuery {
	public MyScoreQuery(Query sunquery) {
		super(sunquery);
	}
	
	private class MyScoreProvider extends CustomScoreProvider {
		private LeafReader reader;
		private Set<String> fieldsToLoad;
		
		public MyScoreProvider(LeafReaderContext context) {
			super(context);
			reader = context.reader();
			fieldsToLoad = new HashSet<>();
			fieldsToLoad.add(LuceneConfig.FIELD_PAGERANK);
		}
		
		@Override
		public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
			Document document = reader.document(doc, fieldsToLoad);
			IndexableField field = document.getField(LuceneConfig.FIELD_PAGERANK);
			float influence;
			if (ScoreConfig.WITH_PAGERANK)
				influence = field.numericValue().floatValue() * ScoreConfig.COEF_PAGERANK + ScoreConfig.BIAS_PAGERANK;
			else
				influence = 1.0f;
			return subQueryScore * influence;
		}
	}
	
	@Override
	protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) throws IOException {
		return new MyScoreProvider(context);
	}
}
