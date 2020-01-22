package giedronowicz.lucene3.engine_8_4_1;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MyIndexFiles {
    public MyIndexFiles() {
    }

    public void main(String[] args) {
        String usage = "java org.apache.lucene.demo.IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\nThis indexes the documents in DOCS_PATH, creating a Lucene indexin INDEX_PATH that can be searched with SearchFiles";
        String indexPath = "index";
        String docsPath = null;
        boolean create = true;

        for(int i = 0; i < args.length; ++i) {
            if ("-index".equals(args[i])) {
                indexPath = args[i + 1];
                ++i;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i + 1];
                ++i;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        Path docDir = Paths.get(docsPath);
        if (!Files.isReadable(docDir)) {
            System.out.println("Document directory '" + docDir.toAbsolutePath() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();

        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            if (create) {
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            IndexWriter writer = new IndexWriter(dir, iwc);
            //indexDocs(writer, docDir);
            indexDocs(writer, new File(docsPath) );
            writer.close();
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        } catch (IOException var12) {
            System.out.println(" caught a " + var12.getClass() + "\n with message: " + var12.getMessage());
        }

    }

    void indexDocs(IndexWriter writer, File dir) throws IOException {
        // do not try to index files that cannot be read
        if (dir.canRead()) {

            FileInputStream fis = new FileInputStream(new File(dir, "doc.index"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {

                    String[] docInfo = line.split("\\|");
                    int docId = Integer.parseInt(docInfo[0]);
                    String url = docInfo[1];
                    String contentType = docInfo[2];

                    File file = new File(dir, "doc." + docId);

                    appendDoc(docId, url, contentType, file, writer);
                }
            } finally {
                fis.close();
            }
        }
    }

    public void appendDoc(int docId, String url, String contentType, File file, IndexWriter writer) throws IOException {
        FileInputStream docFis = new FileInputStream(file);

        try {

            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:
            Field urlField = new StringField("url", url, Field.Store.YES);
            doc.add(urlField);
            Field pathField = new StringField("path", file.getAbsolutePath(), Field.Store.YES);
            doc.add(pathField);

            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(docFis, "UTF-8"))));

            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file+", url "+url);
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been indexed) so
                // we use updateDocument instead to replace the old one matching the exact
                // path, if present:
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.getPath()), doc);
            }

        } finally {
            docFis.close();
        }
    }


    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path, new LinkOption[0])) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException var4) {
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }

    }

    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        InputStream stream = Files.newInputStream(file);
        Throwable var5 = null;

        try {
            Document doc = new Document();
            Field pathField = new StringField("path", file.toString(), Store.YES);
            doc.add(pathField);
            doc.add(new LongPoint("modified", new long[]{lastModified}));
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        } catch (Throwable var15) {
            var5 = var15;
            throw var15;
        } finally {
            if (stream != null) {
                if (var5 != null) {
                    try {
                        stream.close();
                    } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                    }
                } else {
                    stream.close();
                }
            }

        }

    }
}
