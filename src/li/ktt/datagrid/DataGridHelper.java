package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.DataGrid;
import li.ktt.settings.ExtractorProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class DataGridHelper {

    private final String schemaName;

    private final String tableName;

    private final List<Column> filteredColumns;

    private final List<Row> rows;

    public DataGridHelper(ExtractorProperties extractorProperties, final DataGrid dataGrid) {
        this.schemaName = initSchemaName(dataGrid);
        this.tableName = initTableName(dataGrid);
        String excludedColumns = extractorProperties.getExcludeColumns();
        this.filteredColumns = initFilteredColumns(excludedColumns, getSelectedColumns(dataGrid));
        this.rows = getSelectedRows(dataGrid);
    }


    public DataGridHelper(String schemaName, String tableName, List<Column> filteredColumns, List<Row> rows) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.filteredColumns = filteredColumns;
        this.rows = rows;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getFilteredColumns() {
        return filteredColumns;
    }

    public List<Row> getRows() {
        return rows;
    }

    @NotNull
    private List<Column> getSelectedColumns(final DataGrid dataGrid) {
        return dataGrid.getDataModel().getColumns(dataGrid.getSelectionModel().getSelectedColumns());
    }

    @NotNull
    public List<Row> getSelectedRows(final DataGrid dataGrid) {
        return dataGrid.getDataModel().getRows(dataGrid.getSelectionModel().getSelectedRows());
    }

    @Nullable
    private String initTableName(final DataGrid dataGrid) {
        final List<Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).table;
        if ((name == null || name.isEmpty()) && dataGrid.getDatabaseTable() != null) {
            return dataGrid.getDatabaseTable().getName();
        }
        return name;
    }

    @Nullable
    private String initSchemaName(final DataGrid dataGrid) {
        final List<Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).schema;
        if ((name == null || name.isEmpty()) && dataGrid.getDatabaseTable() != null) {
            return dataGrid.getDatabaseTable().getSchema();
        }
        return name;
    }

    private List<Column> initFilteredColumns(String excludedColumns, final List<Column> allColumns) {
        List<Column> filtered = new LinkedList<Column>();
        List<Pattern> excludedColumnPatterns = initPatterns(excludedColumns);
        for (final Column column : allColumns) {
            boolean canBeAdded = true;
            for (final Pattern pattern : excludedColumnPatterns) {
                if (pattern.matcher(this.tableName + "." + column.name).matches()) {
                    canBeAdded = false;
                    break;
                }
            }
            if (canBeAdded) {
                filtered.add(column);
            }
        }
        return filtered;
    }

    private List<Pattern> initPatterns(String excludedColumns) {
        List<Pattern> patterns = new LinkedList<Pattern>();
        if (excludedColumns != null && !excludedColumns.isEmpty()) {
            for (String line : excludedColumns.split("\n")) {
                Pattern pattern = Pattern.compile(line);
                patterns.add(pattern);
            }
        }
        return patterns;
    }

}
