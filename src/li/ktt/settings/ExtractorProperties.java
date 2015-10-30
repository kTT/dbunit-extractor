package li.ktt.settings;

public class ExtractorProperties {

    private boolean includeSchema;

    private boolean skipNull;

    private boolean skipEmpty;

    private String excludeColumns;

    public ExtractorProperties() {
    }

    public ExtractorProperties(boolean includeSchema, boolean skipNull, boolean skipEmpty, String excludeColumns) {
        this.includeSchema = includeSchema;
        this.skipNull = skipNull;
        this.skipEmpty = skipEmpty;
        this.excludeColumns = excludeColumns;
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

}
