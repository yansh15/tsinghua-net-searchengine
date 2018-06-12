package xin.yansh.course.searchengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
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
        String query = request.getParameter("query").trim();
        String pageString = request.getParameter("page").trim();
        String htmlString = request.getParameter("html").trim();
        String pdfString = request.getParameter("pdf").trim();
        String wordString = request.getParameter("word").trim();
        String domain = request.getParameter("domain").trim();
        String notString = request.getParameter("not").trim();
        String[] not = notString.equals("") ? new String[0] : notString.split("\\s+");
        PrintWriter out = null;
        if (query == null)
            return;
        try {
            List<Term> terms = HanLP.segment(query);
            int page = Integer.parseInt(pageString);
            boolean html = Boolean.parseBoolean(htmlString);
            boolean pdf = Boolean.parseBoolean(pdfString);
            boolean word = Boolean.parseBoolean(wordString);
            System.out.printf("query: %s, page: %d, html: %s, pdf: %s, word: %s, domain: %s, not: %s", query, page, html, pdf, word, domain, not.toString());
            TopDocs result = searcher.searchQuery(query, page, pdf, word, html, not, domain);
            out = response.getWriter();
            JSONObject object = new JSONObject();
            object.put("totalHits", result.totalHits);
            JSONArray documents = new JSONArray();
            for (ScoreDoc doc : result.scoreDocs) {
                JSONObject document = new JSONObject();
                document.put("url", searcher.getUrl(doc));
                document.put("title", searcher.getTitle(doc));
                document.put("contentType", searcher.getContentType(doc));
                String content = searcher.getContent(doc).length() > 2000 ? searcher.getContent(doc).substring(0, 2000) : searcher.getContent(doc);
                StringBuilder builder = new StringBuilder();
                for (String s : HanLP.extractSummary(content, 5))
                    builder.append(s.trim()).append('。');
                document.put("content", builder.length() > 130 ? builder.substring(0, 130) : builder.toString());
                documents.add(document);
            }
            object.put("documents", documents);
            JSONArray ts = new JSONArray();
            for (Term term : terms)
                ts.add(term.toString());
            object.put("terms", ts);
            out.append(object.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
