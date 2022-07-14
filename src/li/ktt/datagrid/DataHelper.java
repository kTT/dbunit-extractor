package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.GridRow;

import java.util.List;

public interface DataHelper {
    String getSchemaName();

    String getTableName();

    List<GridColumn> getFilteredColumns();

    List<GridRow> getRows();
}
