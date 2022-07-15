package li.ktt.datagrid;

import com.intellij.database.datagrid.DataGrid;
import com.intellij.database.datagrid.DataGridUtil;
import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.GridModel;
import com.intellij.database.datagrid.GridRow;
import com.intellij.database.datagrid.JdbcGridColumn;
import com.intellij.database.model.DasTable;
import com.intellij.database.run.ui.DataAccessType;
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

    private final List<GridColumn> filteredColumns;

    private final List<GridRow> rows;

    private final ExcludedColumns excludedColumns;

    public DataGridHelper(ExtractorProperties extractorProperties, final DataGrid dataGrid) {
        this.schemaName = initSchemaName(dataGrid);
        this.tableName = initTableName(dataGrid);
        String excludedColumnsString = extractorProperties.getExcludeColumns();
        excludedColumns = new ExcludedColumns(excludedColumnsString);
        this.filteredColumns = initFilteredColumns(getSelectedColumns(dataGrid));
        this.rows = getSelectedRows(dataGrid);
    }


    public DataGridHelper(String schemaName, String tableName, List<GridColumn> filteredColumns, List<GridRow> rows) {
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
    public List<GridColumn> getFilteredColumns() {
        return filteredColumns;
    }

    @Override
    public List<GridRow> getRows() {
        return rows;
    }

    @NotNull
    private List<GridRow> getSelectedRows(final DataGrid dataGrid) {
        return getDataModel(dataGrid).getRows(dataGrid.getSelectionModel().getSelectedRows());
    }

    @NotNull
    private GridModel<GridRow, GridColumn> getDataModel(final DataGrid dataGrid) {
        return dataGrid.getDataModel(DataAccessType.DATABASE_DATA);
    }

    @NotNull
    private List<GridColumn> getSelectedColumns(final DataGrid dataGrid) {
        return getDataModel(dataGrid).getColumns(dataGrid.getSelectionModel().getSelectedColumns());
    }

    @Nullable
    private String initTableName(final DataGrid dataGrid) {
        DasTable table = DataGridUtil.getDatabaseTable(dataGrid);
        final List<GridColumn> columns = getDataModel(dataGrid).getColumns();
        String name = columns.isEmpty() || !(columns.get(0) instanceof JdbcGridColumn) ? null : ((JdbcGridColumn) columns.get(0)).getTable();
        if ((name == null || name.isEmpty()) && table != null) {
            return table.getName();
        }
        return name;
    }

    @Nullable
    private String initSchemaName(final DataGrid dataGrid) {
        DasTable table = DataGridUtil.getDatabaseTable(dataGrid);
        final List<GridColumn> columns = getDataModel(dataGrid).getColumns();
        String name = columns.isEmpty() || !(columns.get(0) instanceof JdbcGridColumn) ? null : ((JdbcGridColumn) columns.get(0)).getSchema();
        if (StringUtil.isEmpty(name) && table != null && table.getDasParent() != null) {
            name = table.getDasParent().getName();
        }
        return name;
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
