package li.ktt;

import com.intellij.database.DatabaseDataKeys;
import com.intellij.database.datagrid.DataGrid;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.util.ui.TextTransferable;
import li.ktt.datagrid.DataGridHelper;
import li.ktt.settings.ExtractorProperties;
import li.ktt.settings.ProjectSettings;
import li.ktt.xml.XmlGenerator;
import li.ktt.xml.XmlOutput;
import org.jetbrains.annotations.NotNull;

public class CopyToDbUnit extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        DataGrid dataGrid = DatabaseDataKeys.DATA_GRID_KEY.getData(dataContext);
        ExtractorProperties extractorProperties = ProjectSettings.getExtractorProperties(dataContext);
        XmlOutput xmlOutput = actionPerformed(extractorProperties, dataGrid, dataContext);
        copyOutput(xmlOutput);
        showPopup(dataContext, xmlOutput);
    }

    public XmlOutput actionPerformed(ExtractorProperties extractorProperties, DataGrid dataGrid, DataContext dataContext) {
        XmlOutput result = null;
        if (dataGrid != null) {
            DataGridHelper data = new DataGridHelper(extractorProperties, dataGrid);
            XmlGenerator xmlGenerator = new XmlGenerator(extractorProperties, data);
            xmlGenerator.appendRows();
            result = xmlGenerator.getOutput();
        }
        return result;
    }

    private void copyOutput(XmlOutput xmlOutput) {
        CopyPasteManager.getInstance().setContents(new TextTransferable(xmlOutput.getText()));
    }

    private void showPopup(DataContext dataContext, XmlOutput xmlOutput) {
        MessageType messageType = MessageType.INFO;
        String htmlMessage = "";
        if (xmlOutput != null && xmlOutput.getRowSize() > 0 && xmlOutput.getColumnsSize() > 0) {
            if (xmlOutput.getTableName() == null || xmlOutput.getTableName().isEmpty()) {
                messageType = MessageType.WARNING;
                htmlMessage += "Table name is missing. Please try to synchronize database connection. <br/>";
            }
            htmlMessage += "Copied: " + xmlOutput.getRowSize() + " entries (selected " + xmlOutput.getColumnsSize() + " columns)";
        } else {
            messageType = MessageType.ERROR;
            if (xmlOutput == null) {
                htmlMessage = "Failed to copy entries. No grid available.";
            } else if (xmlOutput.getRowSize() <= 0) {
                htmlMessage = "No rows selected.";
            } else if (xmlOutput.getColumnsSize() <= 0) {
                htmlMessage = "No columns selected.";
            }
        }
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlMessage, messageType, null)
                .setFadeoutTime(7500)
                .createBalloon().show(JBPopupFactory.getInstance().guessBestPopupLocation(dataContext), Balloon.Position.atRight);
    }

}
