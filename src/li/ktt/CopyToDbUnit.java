package li.ktt;

import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataConsumer;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.TextTransferable;
import li.ktt.settings.ProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class CopyToDbUnit extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());

        DataGrid dataGrid = DatabaseDataKeys.DATA_GRID_KEY.getData(e.getDataContext());
        if (dataGrid != null) {
            final List<DataConsumer.Row> rows = getSelectedRows(dataGrid);
            List<DataConsumer.Column> columns = getSelectedColumns(dataGrid);

            final String tableName = getTableName(dataGrid, columns);

            List<Pattern> patterns = getPatterns(project);

            columns = filterColumns(patterns, tableName, columns);

            StringBuilder builder = new StringBuilder();
            for (final DataConsumer.Row row : rows) {
                addRow(project, columns, tableName, builder, row);
            }
            CopyPasteManager.getInstance().setContents(new TextTransferable(builder.toString()));
        }
    }

    private List<Pattern> getPatterns(final Project project) {
        final String excludeColumns = ProjectSettings.getExcludeColumns(project);
        List<Pattern> patterns = new LinkedList<Pattern>();
        for (String line : excludeColumns.split("\n")) {
            Pattern pattern = Pattern.compile(line);
            patterns.add(pattern);
        }
        return patterns;
    }

    private List<DataConsumer.Column> filterColumns(final List<Pattern> patterns,
                                                    final String tableName,
                                                    final List<DataConsumer.Column> columns) {
        List<DataConsumer.Column> filtered = new LinkedList<DataConsumer.Column>();
        for (final DataConsumer.Column column : columns) {
            boolean canBeAdded = true;
            for (final Pattern pattern : patterns) {
                if (pattern.matcher(tableName + "." + column.name).matches()) {
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

    private void addRow(final Project project,
                        final List<DataConsumer.Column> columns,
                        final String tableName,
                        final StringBuilder builder,
                        final DataConsumer.Row row) {
        builder.append("<").append(tableName).append(" ");

        for (final DataConsumer.Column column : columns) {
            addField(project, builder, row, column);
        }
        builder.append("/>\n");
    }

    private void addField(final Project project,
                          final StringBuilder builder,
                          final DataConsumer.Row row,
                          final DataConsumer.Column column) {
        final Object columnValue = row.values[column.columnNum];
        if (notNullOrNullAllowed(project, columnValue) && notEmptyOrEmptyAllowed(project, columnValue)) {
            builder.append(column.name)
                   .append("=\"");
            if (columnValue != null) {
                builder.append(columnValue);
            }
            builder.append("\" ");
        }
    }

    private boolean notEmptyOrEmptyAllowed(final Project project, final Object columnValue) {
        return (columnValue == null || !String.valueOf(columnValue).isEmpty()) || !ProjectSettings.isSkipEmptyEnabled(project);
    }

    private boolean notNullOrNullAllowed(final Project project, final Object columnValue) {
        return columnValue != null || !ProjectSettings.isSkipNullEnabled(project);
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
