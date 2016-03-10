package li.ktt.settings;

public class ExtractorProperties {

    private boolean includeSchema;

    private boolean skipNull;

    private boolean skipEmpty;

    private String excludeColumns;

    private String selectedDataSourceName;

    public ExtractorProperties() {
    }

    public ExtractorProperties(boolean includeSchema, boolean skipNull, boolean skipEmpty, String excludeColumns, String selectedDataSourceName) {
        this.includeSchema = includeSchema;
        this.skipNull = skipNull;
        this.skipEmpty = skipEmpty;
        this.excludeColumns = excludeColumns;
        this.selectedDataSourceName = selectedDataSourceName;
    }

    public boolean isSkipNull() {
        return skipNull;
    }

    public boolean isSkipEmpty() {
        return skipEmpty;
    }

    public boolean isIncludeSchema() {
        return includeSchema;
    }

    public String getExcludeColumns() {
        return excludeColumns;
    }

    public String getSelectedDataSourceName() {
        return selectedDataSourceName;
    }
}
