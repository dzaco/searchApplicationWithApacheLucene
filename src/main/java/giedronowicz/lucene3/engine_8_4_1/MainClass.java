package giedronowicz.lucene3.engine_8_4_1;

import org.apache.lucene.document.Document;

public class MainClass {
    public static void main(String args[])
    {
        String params[] =
                {
                        "-index", "src\\\\engine\\\\index",
                        "-docs", "src\\\\engine\\\\buissness_domains",
                        "-paging", "5",
                        "-query", "kobiety"
                };

//        MyIndexFiles indexFiles = new MyIndexFiles();
//        indexFiles.main(params);

        MySearchFiles searchFiles = new MySearchFiles();
        MyResult myResult = null;
        try {
            myResult = searchFiles.main(params);

            System.out.println("myRes for Query: " + myResult.getQuery() );
            for( Document doc : myResult.getAllPages() )
            {
                System.out.println( doc.get("url") );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
