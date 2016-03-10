package li.ktt;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DbImplUtil;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import li.ktt.datagrid.ResultSetHelper;
import li.ktt.settings.ExtractorProperties;
import li.ktt.settings.ProjectSettings;
import li.ktt.xml.XmlGenerator;
import li.ktt.xml.XmlOutput;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class QueryToXMLConverter extends PsiElementBaseIntentionAction implements IntentionAction {

    @NotNull
    public String getText() {
        return "Convert query to XML";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull final Project project,
                       final Editor editor,
                       @NotNull final PsiElement psiElement) throws IncorrectOperationException {

        ExtractorProperties extractorProperties =
                ProjectSettings.getExtractorProperties(SimpleDataContext.getProjectContext(project));

        final List<DbDataSource> dataSources = DbPsiFacade.getInstance(project).getDataSources();

        if (dataSources.isEmpty()) {
            showPopup(editor, MessageType.ERROR, "Could not find datasource.");
            return;
        }

        final String selectedDataSourceName = extractorProperties.getSelectedDataSourceName();

        DbDataSource dataSource = null;

        if (StringUtil.isNotEmpty(selectedDataSourceName)) {
            for (final DbDataSource source : dataSources) {
                if (source.getName().equals(selectedDataSourceName)) {
                    dataSource = source;
                    break;
                }
            }
        }

        if (dataSource == null || StringUtil.isEmpty(selectedDataSourceName)) {
            dataSource = dataSources.get(0);
            showPopup(editor, MessageType.INFO, "Using first found datasource: " + dataSource.getName() + ". Please change default one in options.");
        }

        final String query = StringUtil.trim(editor.getSelectionModel().getSelectedText());

        try (final Connection connection = DbImplUtil.getConnection(dataSource)) {
            try (final Statement statement = connection == null ? null : connection.createStatement()) {
                try (final ResultSet resultSet = statement == null ? null : statement.executeQuery(
                        query)) {

                    if (resultSet == null) {
                        showPopup(editor, MessageType.ERROR, "Connection error");
                        return;
                    }

                    final ResultSetMetaData metaData = resultSet.getMetaData();
                    final List<Column> columns = constructColumns(metaData);
                    final List<Row> rows = constructRows(metaData, resultSet);

                    final ResultSetHelper resultSetHelper =
                            new ResultSetHelper(extractorProperties,
                                                metaData.getSchemaName(1),
                                                metaData.getTableName(1),
                                                columns,
                                                rows);
                    XmlGenerator xmlGenerator =
                            new XmlGenerator(extractorProperties, resultSetHelper);
                    xmlGenerator.appendRows();

                    replaceSelection(editor, xmlGenerator.getOutput());
                } catch (SQLException e) {
                    showPopup(editor, MessageType.ERROR, e.getLocalizedMessage());
                }
            }
        } catch (Exception e) {
            showPopup(editor, MessageType.ERROR, e.getLocalizedMessage());
        }
    }

    @NotNull
    private List<Row> constructRows(final ResultSetMetaData metaData,
                                    final ResultSet resultSet) throws SQLException {
        final List<Row> rows = new LinkedList<>();
        int rowNum = 0;
        while (resultSet.next()) {
            List<Object> values = new LinkedList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                values.add(resultSet.getString(i));
            }
            rows.add(new Row(rowNum - 1, values.toArray()));
            ++rowNum;
        }
        return rows;
    }

    @NotNull
    private List<Column> constructColumns(final ResultSetMetaData metaData)
            throws SQLException {
        final List<Column> columns = new LinkedList<>();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columns.add(new Column(i - 1,
                                   metaData.getColumnName(i),
                                   metaData.getColumnType(i),
                                   metaData.getColumnTypeName(i),
                                   metaData.getColumnClassName(i)));
        }
        return columns;
    }

    private void replaceSelection(final Editor editor, final XmlOutput xmlOutput) {
        final SelectionModel selectionModel = editor.getSelectionModel();
        editor.getDocument()
              .replaceString(selectionModel.getSelectionStart(),
                             selectionModel.getSelectionEnd(),
                             xmlOutput.getText());
        editor.getSelectionModel().removeSelection();
    }

    @Override
    public boolean isAvailable(@NotNull final Project project,
                               final Editor editor,
                               @NotNull final PsiElement psiElement) {
        return editor.getDocument().getText().startsWith("<dataset>");
    }

    private void showPopup(final Editor editor,
                           final MessageType messageType,
                           final String message) {
        JBPopupFactory.getInstance()
                      .createHtmlTextBalloonBuilder(message, messageType, null)
                      .setFadeoutTime(7500)
                      .createBalloon()
                      .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor),
                            Balloon.Position.atRight);
    }
}
