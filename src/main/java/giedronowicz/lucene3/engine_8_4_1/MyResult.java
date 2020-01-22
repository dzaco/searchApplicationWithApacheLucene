package giedronowicz.lucene3.engine_8_4_1;

import org.apache.lucene.document.Document;

import java.util.List;

public class MyResult {

    private int hitsPerPage;
//    private List<Document> domain;
//    private List<List<Document>> window;
    private List<Document> allPages;
    private String query;

    public MyResult(List<Document> allPages , int hitsPerPage, String query)
    {
        this.hitsPerPage = hitsPerPage;
        this.allPages = allPages;
        this.query = query;


//        int start = 0;
//        int end = Math.min(allPages.size(), start + hitsPerPage);
//        int numPages =
//
//        for( int p = 0; p < allPages )
//        {
//            for( int i = start; i < end; i++)
//            {
//                domain.add()
//            }
//        }

    }

    public List<Document> getAllPages() {
        return allPages;
    }

    public String getQuery() {
        return query;
    }
}
