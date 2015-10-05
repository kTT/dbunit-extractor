package li.ktt.xml;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import li.ktt.datagrid.DataGridHelper;
import li.ktt.settings.ExtractorProperties;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlGeneratorTest {

    private static final String NO_EXCLUDED_COLUMNS = "";

    private static final List<Column> columns = new ArrayList<Column>();

    private static final List<Row> rows = new ArrayList<Row>();

    @BeforeClass
    public static void before() {
        columns.add(createColumn(1, "Name"));
        columns.add(createColumn(2, "Value"));
        columns.add(createColumn(3, "Type"));

        rows.add(createRow(1, "a1", "Super1", "B12", "g1", "..."));
        rows.add(createRow(2, "a3", "Super3", "j55", "g12"));
        rows.add(createRow(3, "a4", "Super3", "with null", null));
        rows.add(createRow(4, "a4", "Super3", "with empty value", ""));
    }

    @Test
    public void shouldReturnAllRowsWithSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(true, true, true, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema2", "Table2", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<superSchema2.Table2 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with null\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with empty value\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingEmptiesWithSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(true, true, false, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema2", "Table2", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<superSchema2.Table2 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with null\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with empty value\" Type=\"\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingNullsWithSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(true, false, true, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema2", "Table2", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<superSchema2.Table2 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with null\" Type=\"\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with empty value\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingNullsAndEmptiesWithSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(true, false, false, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema2", "Table2", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<superSchema2.Table2 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with null\" Type=\"\" />\n" +
                "<superSchema2.Table2 Name=\"Super3\" Value=\"with empty value\" Type=\"\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsWithoutSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(false, true, true, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema1", "Table1", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<Table1 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with null\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with empty value\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingEmptiesWithoutSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(false, true, false, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema1", "Table1", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows();

        // then
        String expectedResult = "<Table1 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with null\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with empty value\" Type=\"\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingNullsWithoutSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(false, false, true, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema1", "Table1", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows();

        // then
        String expectedResult = "<Table1 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with null\" Type=\"\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with empty value\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    @Test
    public void shouldReturnAllRowsIncludingNullsAndEmptiesWithoutSchema() {
        // given
        ExtractorProperties extractorProperties = new ExtractorProperties(false, false, false, NO_EXCLUDED_COLUMNS);
        DataGridHelper data = new DataGridHelper("superSchema1", "Table1", columns, rows);
        XmlGenerator generator = new XmlGenerator(extractorProperties, data);

        // when
        generator.appendRows(rows);

        // then
        String expectedResult = "<Table1 Name=\"Super1\" Value=\"B12\" Type=\"g1\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"j55\" Type=\"g12\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with null\" Type=\"\" />\n" +
                "<Table1 Name=\"Super3\" Value=\"with empty value\" Type=\"\" />\n";
        assertEquals(expectedResult, generator.getOutput());
    }

    private static Row createRow(int num, String... values) {
        return new Row(num, values);
    }

    private static Column createColumn(int columnNum, String name) {
        return new Column(columnNum, name, 0, "typeName", "String");
    }

}
