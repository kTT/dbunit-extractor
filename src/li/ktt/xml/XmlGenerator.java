package li.ktt.xml;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import li.ktt.datagrid.DataGridHelper;
import li.ktt.settings.ExtractorProperties;

import java.util.List;

public class XmlGenerator {

    private ExtractorProperties extractorProperties;

    private DataGridHelper data;

    private final StringBuilder builder;

    public XmlGenerator(ExtractorProperties extractorProperties, DataGridHelper data) {
        this(extractorProperties, data, new StringBuilder());
    }

    public XmlGenerator(ExtractorProperties extractorProperties, DataGridHelper data, StringBuilder builder) {
        this.extractorProperties = extractorProperties;
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
        if (extractorProperties.isIncludeSchema()) {
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
        return (columnValue == null || !String.valueOf(columnValue).isEmpty()) || !extractorProperties.isSkipEmpty();
    }

    private boolean notNullOrNullAllowed(final Object columnValue) {
        return columnValue != null || !extractorProperties.isSkipNull();
    }

}
