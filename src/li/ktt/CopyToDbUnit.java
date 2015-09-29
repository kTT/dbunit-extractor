package li.ktt;

import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataConsumer;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.util.ui.TextTransferable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopyToDbUnit extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataGrid dataGrid = DatabaseDataKeys.DATA_GRID_KEY.getData(e.getDataContext());
        if (dataGrid != null) {
            final List<DataConsumer.Row> rows = getSelectedRows(dataGrid);
            final List<DataConsumer.Column> columns = getSelectedColumns(dataGrid);

            final String tableName = getTableName(dataGrid, columns);

            StringBuilder builder = new StringBuilder();
            for (final DataConsumer.Row row : rows) {
                builder.append("<").append(tableName).append(" ");

                for (final DataConsumer.Column column : columns) {
                    builder.append(column.name)
                           .append("=\"")
                           .append(row.values[column.columnNum])
                           .append("\" ");
                }
                builder.append("/>\n");
            }
            CopyPasteManager.getInstance().setContents(new TextTransferable(builder.toString()));
        }
    }

    @Nullable
    private String getTableName(final DataGrid dataGrid,
                                @NotNull final List<DataConsumer.Column> columns) {
        String name = columns.isEmpty() ? null : columns.get(0).table;
        if ((name == null || name.isEmpty()) && dataGrid.getDatabaseTable() != null) {
            return dataGrid.getDatabaseTable().getName();
        }
        return name;
    }

    @NotNull
    private List<DataConsumer.Column> getSelectedColumns(@NotNull final DataGrid dataGrid) {
        return dataGrid.getDataModel().getColumns(dataGrid.getSelectionModel().getSelectedColumns());
    }

    @NotNull
    private List<DataConsumer.Row> getSelectedRows(@NotNull final DataGrid dataGrid) {
        return dataGrid.getDataModel().getRows(dataGrid.getSelectionModel().getSelectedRows());
    }
}
