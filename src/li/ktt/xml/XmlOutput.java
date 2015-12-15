package li.ktt.xml;

public class XmlOutput {

    private String text;
    private int rowSize;
    private int columnsSize;
    private String tableName;

    public XmlOutput(String text, int rowSize, int columnsSize, String tableName) {
        this.text = text;
        this.rowSize = rowSize;
        this.columnsSize = columnsSize;
        this.tableName = tableName;
    }

    public String getText() {
        return this.text;
    }

    public int getRowSize() {
        return this.rowSize;
    }

    public int getColumnsSize() {
        return columnsSize;
    }

    public String getTableName() {
        return tableName;
    }

}
