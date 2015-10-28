package li.ktt;

import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.util.ui.TextTransferable;
import li.ktt.datagrid.DataGridHelper;
import li.ktt.settings.ExtractorProperties;
import li.ktt.settings.ProjectSettings;
import li.ktt.xml.XmlGenerator;
import org.jetbrains.annotations.NotNull;

public class CopyToDbUnit extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        DataGrid dataGrid = DatabaseDataKeys.DATA_GRID_KEY.getData(dataContext);
        ExtractorProperties extractorProperties = ProjectSettings.getExtractorProperties(dataContext);
        actionPerformed(extractorProperties, dataGrid);
    }

    public void actionPerformed(ExtractorProperties extractorProperties, DataGrid dataGrid) {
        if (dataGrid != null) {
            DataGridHelper data = new DataGridHelper(extractorProperties, dataGrid);
            XmlGenerator xmlGenerator = new XmlGenerator(extractorProperties, data);
            xmlGenerator.appendRows();
            CopyPasteManager.getInstance().setContents(new TextTransferable(xmlGenerator.getOutput()));
        }
    }

}
