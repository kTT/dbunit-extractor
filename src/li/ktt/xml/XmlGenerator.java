package li.ktt.xml;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.extractors.tz.TimeZonedTimestamp;
import com.intellij.database.remote.jdbc.LobInfo;
import li.ktt.datagrid.DataHelper;
import li.ktt.settings.ExtractorProperties;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.intellij.openapi.util.text.StringUtil.escapeXml;

public class XmlGenerator {

    private ExtractorProperties extractorProperties;

    private DataHelper data;

    private final StringBuilder builder;

    private int rowSize;

    private final int columnsSize;

    private final String tableName;

    public XmlGenerator(ExtractorProperties extractorProperties, DataHelper data) {
        this(extractorProperties, data, new StringBuilder());
    }

    public XmlGenerator(ExtractorProperties extractorProperties, DataHelper data, StringBuilder builder) {
        this.extractorProperties = extractorProperties;
        this.data = data;
        this.builder = builder;
        this.columnsSize = data.getFilteredColumns().size();
        this.tableName = data.getTableName();
    }

    public XmlOutput getOutput() {
        return new XmlOutput(this.builder.toString(), this.rowSize, this.columnsSize, this.tableName);
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
        this.rowSize++;
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
        final String columnValue = extractStringValue(row.values[column.columnNum]);
        if (notNullOrNullAllowed(columnValue) && notEmptyOrEmptyAllowed(columnValue)) {
            builder.append(column.name).append("=\"");
            if (columnValue != null) {
                builder.append(escapeXml(columnValue));
            }
            builder.append("\" ");
        }
    }

    private String extractStringValue(final Object value) {
        if (value instanceof TimeZonedTimestamp) {
            return ((TimeZonedTimestamp) value).getValue().toString();
        }
        if (value instanceof Timestamp) {
            return Timestamp.valueOf(LocalDateTime.ofInstant(((Timestamp) value).toInstant(), ZoneOffset.UTC)).toString();
        }
        if (value instanceof LobInfo.ClobInfo) {
            return ((LobInfo.ClobInfo) value).data;
        }
        return value != null ? value.toString() : null;
    }

    private boolean notEmptyOrEmptyAllowed(final Object columnValue) {
        return (columnValue == null || !String.valueOf(columnValue).isEmpty()) || !extractorProperties.isSkipEmpty();
    }

    private boolean notNullOrNullAllowed(final Object columnValue) {
        return columnValue != null || !extractorProperties.isSkipNull();
    }

}
