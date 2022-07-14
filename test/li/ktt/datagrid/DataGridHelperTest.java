package li.ktt.datagrid;

import com.intellij.database.datagrid.*;
import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.run.ui.DataAccessType;
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
    private SelectionModel<GridRow, GridColumn> selectionModelMock;

    @Mock
    private ModelIndexSet<GridRow> selectedRowsMock;

    @Mock
    private ModelIndexSet<GridColumn> selectedColumnsMock;

    @Mock
    private GridModel<GridRow, GridColumn> dataModelMock;

    private final List<GridRow> sampleRows = new ArrayList<>();

    private final List<GridColumn> sampleColumns = new ArrayList<>();

    private final ExtractorProperties defaultProperties = new ExtractorProperties(true, true, true, "", null);

    @Before
    public void before() {
        // dataGrid
        when(dataGridMock.getDataModel(DataAccessType.DATABASE_DATA)).thenReturn(dataModelMock);
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
        List<GridRow> result = data.getRows();

        // then
        assertEquals(sampleRows, result);
    }

    @Test
    public void shouldReturnFullListOfColumnIfExcludedPatternIsEmpty() {
        // when
        DataGridHelper data = new DataGridHelper(defaultProperties, dataGridMock);

        // then
        List<GridColumn> result = data.getFilteredColumns();
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
        List<GridColumn> localSampleColumns = new ArrayList<>();
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
