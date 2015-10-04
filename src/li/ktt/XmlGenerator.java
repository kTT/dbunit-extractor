package li.ktt;

import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.DataConsumer.Column;

import java.util.List;

public class XmlGenerator {

    private XmlConfiguration xmlConfiguration;

    private DataGridHelper data;

    private final StringBuilder builder;

    public XmlGenerator(XmlConfiguration xmlConfiguration, DataGridHelper data) {
        this(xmlConfiguration, data, new StringBuilder());
    }

    public XmlGenerator(XmlConfiguration xmlConfiguration, DataGridHelper data, StringBuilder builder) {
        this.xmlConfiguration = xmlConfiguration;
        this.data = data;
        this.builder = builder;
    }

    public String getOutput() {
        return this.builder.toString();
    }

    public XmlGenerator appendRows() {
        for (Row row : data.getRows()) {
            appendRow(row);
        }
        return this;
    }

    public XmlGenerator appendRows(List<Row> rows) {
        for (Row row : rows) {
            appendRow(row);
        }
        return this;
    }

    public void appendRow(final Row row) {
        builder.append("<");
        if (xmlConfiguration.isIncludeSchemaEnabled()) {
            builder.append(data.getSchemaName()).append(".");
        }
        builder.append(data.getTableName()).append(" ");

        for (final Column column : data.getFilteredColumns()) {
            appendField(builder, row, column);
        }
        builder.append("/>\n");
    }

    private void appendField(final StringBuilder builder,
                             final Row row,
                             final Column column) {
        final Object columnValue = row.values[column.columnNum];
        if (notNullOrNullAllowed(columnValue) && notEmptyOrEmptyAllowed(columnValue)) {
            builder.append(column.name).append("=\"");
            if (columnValue != null) {
                builder.append(columnValue);
            }
            builder.append("\" ");
        }
    }

    private boolean notEmptyOrEmptyAllowed(final Object columnValue) {
        return (columnValue == null || !String.valueOf(columnValue).isEmpty()) || !xmlConfiguration.isSkipEmptyEnabled();
    }

    private boolean notNullOrNullAllowed(final Object columnValue) {
        return columnValue != null || !xmlConfiguration.isSkipNullEnabled();
    }

}
