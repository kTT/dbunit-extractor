package li.ktt;

import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.TextTransferable;
import org.jetbrains.annotations.NotNull;

public class CopyToDbUnit extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        DataGrid dataGrid = DatabaseDataKeys.DATA_GRID_KEY.getData(dataContext);

        if (dataGrid != null) {
            DataGridHelper data = new DataGridHelper(project, dataGrid);
            XmlConfiguration xmlConfiguration = new XmlConfiguration(project);
            XmlGenerator xmlGenerator = new XmlGenerator(xmlConfiguration, data);
            xmlGenerator.appendRows();
            CopyPasteManager.getInstance().setContents(new TextTransferable(xmlGenerator.getOutput()));
        }
    }

}
