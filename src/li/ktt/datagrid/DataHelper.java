package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer;

import java.util.List;

public interface DataHelper {
    String getSchemaName();

    String getTableName();

    List<DataConsumer.Column> getFilteredColumns();

    List<DataConsumer.Row> getRows();
}
