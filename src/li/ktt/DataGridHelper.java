package li.ktt;

import com.intellij.database.datagrid.DataConsumer;
import com.intellij.database.datagrid.DataGrid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DataGridHelper {
    private DataGrid dataGrid;

    public DataGridHelper(final DataGrid dataGrid) {
        this.dataGrid = dataGrid;
    }

    @NotNull
    public List<DataConsumer.Column> getSelectedColumns() {
        return dataGrid.getDataModel().getColumns(dataGrid.getSelectionModel().getSelectedColumns());
    }

    @NotNull
    public List<DataConsumer.Row> getSelectedRows() {
        return dataGrid.getDataModel().getRows(dataGrid.getSelectionModel().getSelectedRows());
    }

    @Nullable
    public String getTableName() {
        final List<DataConsumer.Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).table;
        if ((name == null || name.isEmpty()) && dataGrid.getDatabaseTable() != null) {
            return dataGrid.getDatabaseTable().getName();
        }
        return name;
    }

    @Nullable
    public String getSchemaName() {
        final List<DataConsumer.Column> columns = dataGrid.getDataModel().getColumns();
        String name = columns.isEmpty() ? null : columns.get(0).schema;
        if ((name == null || name.isEmpty()) && dataGrid.getDatabaseTable() != null) {
            return dataGrid.getDatabaseTable().getSchema();
        }
        return name;
    }
}
