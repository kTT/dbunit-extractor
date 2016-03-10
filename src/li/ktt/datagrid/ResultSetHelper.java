package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import li.ktt.settings.ExcludedColumns;
import li.ktt.settings.ExtractorProperties;

import java.util.LinkedList;
import java.util.List;

public class ResultSetHelper implements DataHelper {

    private final String schemaName;

    private final String tableName;

    private final List<Column> filteredColumns;

    private final List<Row> rows;

    private final ExcludedColumns excludedColumns;

    public ResultSetHelper(ExtractorProperties extractorProperties,
                           final String schemaName,
                           final String tableName,
                           final List<Column> columns,
                           final List<Row> rows) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        String excludedColumnsString = extractorProperties.getExcludeColumns();
        excludedColumns = new ExcludedColumns(excludedColumnsString);
        this.filteredColumns = initFilteredColumns(columns);
        this.rows = rows;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public List<Column> getFilteredColumns() {
        return filteredColumns;
    }

    @Override
    public List<Row> getRows() {
        return rows;
    }

    private List<Column> initFilteredColumns(final List<Column> allColumns) {
        List<Column> filtered = new LinkedList<>();
        for (final Column column : allColumns) {
            if (this.excludedColumns.canBeAdded(this.tableName + "." + column.name)) {
                filtered.add(column);
            }
        }
        return filtered;
    }
}
