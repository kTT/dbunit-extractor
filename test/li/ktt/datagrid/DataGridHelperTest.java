package li.ktt.datagrid;

import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.database.datagrid.GridModel;
import com.intellij.database.datagrid.ModelIndexSet;
import com.intellij.database.datagrid.SelectionModel;
import li.ktt.settings.ExtractorProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataGridHelperTest {

    @Mock
    private DataGrid dataGridMock;

    @Mock
    private SelectionModel<Row, Column> selectionModelMock;

    @Mock
    private ModelIndexSet<Row> selectedRowsMock;

    @Mock
    private ModelIndexSet<Column> selectedColumnsMock;

    @Mock
    private GridModel<Row, Column> dataModelMock;

    private final List<Row> sampleRows = new ArrayList<Row>();

    private final List<Column> sampleColumns = new ArrayList<Column>();

    private final ExtractorProperties defaultProperties = new ExtractorProperties(true, true, true, "", null);

    @Before
    public void before() {
        // dataGrid
        when(dataGridMock.getDataModel()).thenReturn(dataModelMock);
        when(dataGridMock.getSelectionModel()).thenReturn(selectionModelMock);

        // selectionModel
        when(selectionModelMock.getSelectedRows()).thenReturn(selectedRowsMock);
        when(selectionModelMock.getSelectedColumns()).thenReturn(selectedColumnsMock);

        // dataModel
        when(dataModelMock.getRows(selectedRowsMock)).thenReturn(sampleRows);
        when(dataModelMock.getColumns(selectedColumnsMock)).thenReturn(sampleColumns);

        sampleRows.add(mock(Row.class));
        sampleRows.add(mock(Row.class));


        sampleColumns.add(mock(Column.class));
        sampleColumns.add(mock(Column.class));
        sampleColumns.add(mock(Column.class));
    }

    @Test
    public void shouldReturnAllRows() {
        // given

        DataGridHelper data = new DataGridHelper(defaultProperties, dataGridMock);

        // when
        List<Row> result = data.getRows();

        // then
        assertEquals(sampleRows, result);
    }

    @Test
    public void shouldReturnFullListOfColumnIfExcludedPatternIsEmpty() {
        // when
        DataGridHelper data = new DataGridHelper(defaultProperties, dataGridMock);

        // then
        List<Column> result = data.getFilteredColumns();
        assertEquals(sampleColumns, result);
    }

    @Test
    public void shouldReturnSchemaAndTableAsNullIfFirstColumnSchemaReturnNullAndDatabaseTableNull() {
        // when
        DataGridHelper data = new DataGridHelper(defaultProperties, dataGridMock);

        // then
        assertEquals(null, data.getSchemaName());
        assertEquals(null, data.getTableName());
    }

    @Test
    public void shouldReturnSchemaAndTableNamesFromFirstColumn() {
        // given
        List<Column> localSampleColumns = new ArrayList<Column>();
        localSampleColumns.add(new Column(1, "superCol", 1, "type", "String", 1, 2, "catalog", "schema201", "superTable201"));
        localSampleColumns.add(new Column(2, "superCol", 1, "type", "String", 1, 2, "catalog", "schema203", "superTable201"));
        when(dataModelMock.getColumns()).thenReturn(localSampleColumns);

        // when
        DataGridHelper data = new DataGridHelper(defaultProperties, dataGridMock);

        // then
        assertEquals("schema201", data.getSchemaName());
        assertEquals("superTable201", data.getTableName());
    }

}
