package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.GridRow;
import li.ktt.settings.ExcludedColumns;
import li.ktt.settings.ExtractorProperties;
import net.miginfocom.layout.Grid;

import java.util.LinkedList;
import java.util.List;

public class ResultSetHelper implements DataHelper {

    private final String schemaName;

    private final String tableName;

    private final List<GridColumn> filteredColumns;

    private final List<GridRow> rows;

    private final ExcludedColumns excludedColumns;

    public ResultSetHelper(ExtractorProperties extractorProperties,
                           final String schemaName,
                           final String tableName,
                           final List<GridColumn> columns,
                           final List<GridRow> rows) {
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
    public List<GridColumn> getFilteredColumns() {
        return filteredColumns;
    }

    @Override
    public List<GridRow> getRows() {
        return rows;
    }

    private List<GridColumn> initFilteredColumns(final List<GridColumn> allColumns) {
        List<GridColumn> filtered = new LinkedList<>();
        for (final GridColumn column : allColumns) {
            if (this.excludedColumns.canBeAdded(this.tableName + "." + column.getName())) {
                filtered.add(column);
            }
        }
        return filtered;
    }
}
