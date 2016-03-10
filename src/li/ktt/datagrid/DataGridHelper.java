package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.database.datagrid.DataGridUtil;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.util.text.StringUtil;
import li.ktt.settings.ExcludedColumns;
import li.ktt.settings.ExtractorProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class DataGridHelper implements DataHelper {

    private final String schemaName;

    private final String tableName;

    private final List<Column> filteredColumns;

    private final List<Row> rows;

    private final ExcludedColumns excludedColumns;

    public DataGridHelper(ExtractorProperties extractorProperties, final DataGrid dataGrid) {
        this.schemaName = initSchemaName(dataGrid);
        this.tableName = initTableName(dataGrid);
        String excludedColumnsString = extractorProperties.getExcludeColumns();
        excludedColumns = new ExcludedColumns(excludedColumnsString);
        this.filteredColumns = initFilteredColumns(getSelectedColumns(dataGrid));
        this.rows = getSelectedRows(dataGrid);
    }


    public DataGridHelper(String schemaName, String tableName, List<Column> filteredColumns, List<Row> rows) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.excludedColumns = new ExcludedColumns("");
        this.filteredColumns = filteredColumns;
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

    @NotNull
    private List<Row> getSelectedRows(final DataGrid dataGrid) {
        return dataGrid.getDataModel().getRows(dataGrid.getSelectionModel().getSelectedRows());
    }

    @NotNull
    private List<Column> getSelectedColumns(final DataGrid dataGrid) {
        return dataGrid.getDataModel().getColumns(dataGrid.getSelectionModel().getSelectedColumns());
    }

    @Nullable
    private String initTableName(final DataGrid dataGrid) {
        DasTable table = DataGridUtil.getDatabaseTable(dataGrid);
        final List<Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).table;
        if ((name == null || name.isEmpty()) && table != null) {
            return table.getName();
        }
        return name;
    }

    @Nullable
    private String initSchemaName(final DataGrid dataGrid) {
        DasTable table = DataGridUtil.getDatabaseTable(dataGrid);
        final List<Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).schema;
        if (StringUtil.isEmpty(name) && table != null && table.getDbParent() != null) {
            name = table.getDbParent().getName();
        }
        return name;
    }

    private List<Column> initFilteredColumns(final List<Column> allColumns) {
        List<Column> filtered = new LinkedList<Column>();
        for (final Column column : allColumns) {
            if (this.excludedColumns.canBeAdded(this.tableName + "." + column.name)) {
                filtered.add(column);
            }
        }
        return filtered;
    }

}
