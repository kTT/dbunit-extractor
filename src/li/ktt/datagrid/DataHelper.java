package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;

import java.util.List;

public interface DataHelper {
    String getSchemaName();

    String getTableName();

    List<Column> getFilteredColumns();

    List<Row> getRows();
}
