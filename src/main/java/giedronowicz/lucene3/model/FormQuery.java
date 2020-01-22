package giedronowicz.lucene3.model;

public class FormQuery {

    private String query;

    public FormQuery() {
        this.query = "";
    }
    public FormQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "FormQuery{" +
                "query='" + query + '\'' +
                '}';
    }
}
