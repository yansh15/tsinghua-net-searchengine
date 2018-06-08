package xin.yansh.course.searchengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchServer extends HttpServlet {
	
	private StartSearch searcher;
	
	public SearchServer() throws Exception {
		super();
		searcher = new StartSearch();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("utf-8");
		String query = request.getParameter("query");
		String pageString = request.getParameter("page");
		PrintWriter out = null;
		if (query == null)
			return;
		System.out.println(query);
		try {
			int page = Integer.parseInt(pageString);
			TopDocs result = searcher.searchQuery(query, page);
			out = response.getWriter();
			ArrayList<String> url = new ArrayList<String>();
			out.append(Long.toString(result.totalHits));
			ScoreDoc[] docs = result.scoreDocs;
			for (int i = 0; i < docs.length; ++i)
				url.add(searcher.getUrl(docs[i]));
			out.append(url.toString());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (out != null)
				out.close();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
