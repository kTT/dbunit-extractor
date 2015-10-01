package li.ktt.settings;

public class DbUnitProperties {
    private Boolean skipNull;
    private Boolean skipEmpty;
    private String excludeColumns;

    public Boolean getSkipNull() {
        return skipNull;
    }

    public void setSkipNull(final Boolean skipNull) {
        this.skipNull = skipNull;
    }

    public Boolean getSkipEmpty() {
        return skipEmpty;
    }

    public void setSkipEmpty(final Boolean skipEmpty) {
        this.skipEmpty = skipEmpty;
    }

    public String getExcludeColumns() {
        return excludeColumns;
    }

    public void setExcludeColumns(final String excludeColumns) {
        this.excludeColumns = excludeColumns;
    }
}
