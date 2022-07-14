package li.ktt;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.connection.DGDepartment;
import com.intellij.database.datagrid.DataConsumer.Column;
import com.intellij.database.datagrid.DataConsumer.Row;
import com.intellij.database.datagrid.GridColumn;
import com.intellij.database.datagrid.GridRow;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.remote.jdbc.RemoteDatabaseMetaData;
import com.intellij.database.remote.jdbc.RemoteResultSet;
import com.intellij.database.remote.jdbc.RemoteResultSetMetaData;
import com.intellij.database.remote.jdbc.RemoteStatement;
import com.intellij.database.util.DbImplUtil;
import com.intellij.database.util.GuardedRef;
import com.intellij.injected.editor.EditorWindow;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.sql.psi.SqlSelectStatement;
import com.intellij.util.IncorrectOperationException;
import li.ktt.datagrid.ResultSetHelper;
import li.ktt.settings.ExtractorProperties;
import li.ktt.settings.ProjectSettings;
import li.ktt.xml.XmlGenerator;
import li.ktt.xml.XmlOutput;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class QueryToXMLConverter extends PsiElementBaseIntentionAction implements IntentionAction {

    private int TABLE_SCHEME_INDEX = 2;

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

        final ExtractorProperties extractorProperties =
                ProjectSettings.getExtractorProperties(SimpleDataContext.getProjectContext(project));

        final List<DbDataSource> dataSources = DbPsiFacade.getInstance(project).getDataSources();

        if (dataSources.isEmpty()) {
            showPopup(editor, MessageType.ERROR, "Could not find datasource.");
            return;
        }

        final String selectedDataSourceName = extractorProperties.getSelectedDataSourceName();

        final DbDataSource dataSource = getDataSource(editor, dataSources, selectedDataSourceName);

        final String query;
        if (editor.getSelectionModel().hasSelection()) {
            query = StringUtil.trim(editor.getSelectionModel().getSelectedText());
        } else {
            final SmartPsiElementPointer<SqlSelectStatement> pointer = getNearestPointer(project, psiElement);
            if (pointer != null) {
                query = pointer.getElement().getText();

                final int startOffset = pointer.getRange().getStartOffset();
                int endOffset = pointer.getRange().getEndOffset();

                if (editor.getDocument().getText(TextRange.create(endOffset, endOffset + 1)).equals(";")) {
                    endOffset += 1; // take semicolon after query
                }
                editor.getSelectionModel().setSelection(startOffset, endOffset);
            } else {
                query = null;
            }
        }

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                applySelectionChange(project, editor, extractorProperties, dataSource, query);
            }
        });
    }

    @Nullable
    private DbDataSource getDataSource(final Editor editor,
                                       final List<DbDataSource> dataSources,
                                       final String selectedDataSourceName) {
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
            showPopup(editor,
                      MessageType.INFO,
                      "Using first found datasource: " + dataSource.getName() + ". Please change default one in options.");
        }
        return dataSource;
    }

    private void applySelectionChange(final Project project, final Editor editor,
                                      final ExtractorProperties extractorProperties,
                                      final DbDataSource dataSource, final String query) {
        final String cleanedQuery = query.replaceAll(";$", "");
        RemoteStatement statement = null;
        RemoteResultSet resultSet = null;
        try (final GuardedRef<DatabaseConnection> connection = DbImplUtil.getDatabaseConnection(dataSource, DGDepartment.CODE_GENERATION)) {
             statement = connection == null ? null : connection.get().getRemoteConnection().createStatement();
             resultSet = statement == null ? null : statement.executeQuery(cleanedQuery);

            if (resultSet == null) {
                showPopup(editor, MessageType.ERROR, "Connection error");
                return;
            }

            final RemoteResultSetMetaData metaData = resultSet.getMetaData();

            Set<String> tableNames = getTablesNamesFromQuery(metaData);
            if (tableNames.size() != 1) {
                tableNames = getTablesNamesFromQuery(cleanedQuery);
                if (tableNames.size() != 1) {
                    showPopup(editor, MessageType.ERROR, "Only one table queries are supported.");
                    return;
                }
            }

            final List<GridColumn> columns = constructColumns(metaData);
            final List<GridRow> rows = constructRows(metaData, resultSet);
            final String tableName = tableNames.iterator().next();
            final String schema = StringUtil.isNotEmpty(metaData.getSchemaName(1))
                    ? metaData.getSchemaName(1)
                    : getSchemaName(connection.get(), tableName);

            final ResultSetHelper resultSetHelper =
                    new ResultSetHelper(extractorProperties,
                                        schema,
                                        tableName,
                                        columns,
                                        rows);
            final XmlGenerator xmlGenerator =
                    new XmlGenerator(extractorProperties, resultSetHelper);
            xmlGenerator.appendRows();

            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    replaceSelection(editor, xmlGenerator.getOutput());
                }
            });
        } catch (Exception e) {
            showPopup(editor, MessageType.ERROR, e.getLocalizedMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    showPopup(editor, MessageType.ERROR, "Failed to close connection.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    showPopup(editor, MessageType.ERROR, "Failed to close connection.");
                }
            }
        }
    }

    @NotNull
    private Set<String> getTablesNamesFromQuery(final RemoteResultSetMetaData metaData)
            throws SQLException, RemoteException {
        Set<String> tableNames = new LinkedHashSet<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (StringUtil.isNotEmpty(metaData.getTableName(i))) {
                tableNames.add(metaData.getTableName(i));
            }
        }
        return tableNames;
    }

    @NotNull
    private Set<String> getTablesNamesFromQuery(final String query) {
        Set<String> tableNames = new LinkedHashSet<>();

        int startIndex = query.indexOf("FROM") + 5;
        int endIndex = query.indexOf("WHERE");

        String querySubstring = null;
        if (endIndex > startIndex) {
            querySubstring = query.substring(startIndex, endIndex);
        } else {
            querySubstring = query.substring(startIndex);
        }

        String[] tables = querySubstring.split(",");
        for (String tableName : tables) {
            if (tableName.contains("."))
                tableNames.add(tableName.substring(tableName.indexOf(".") + 1).trim());
            else
                tableNames.add(tableName.trim());
        }

        return tableNames;
    }

    private String getSchemaName(final DatabaseConnection connection, final String tableName) throws SQLException, RemoteException {
        String[] tableType = {"TABLE"};
        final RemoteDatabaseMetaData connectionMetaData = connection.getRemoteMetaData();
        RemoteResultSet result = null;
        try {
            result = connectionMetaData.getTables(null, null, tableName, tableType);
            while (result.next()) {
                return result.getString(TABLE_SCHEME_INDEX);
            }
        } finally {
            result.close();
        }

        // Fallback: Try to take schema from tableName.
        if (tableName.contains(".")) {
            return tableName.substring(0, tableName.indexOf("."));
        }

        return null;
    }

    @NotNull
    private List<GridRow> constructRows(final RemoteResultSetMetaData metaData,
                                    final RemoteResultSet resultSet) throws SQLException, RemoteException {
        final List<GridRow> rows = new LinkedList<>();
        int rowNum = 0;
        while (resultSet.next()) {
            List<Object> values = new LinkedList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                values.add(resultSet.getString(i));
            }
            rows.add(Row.create(rowNum - 1, values.toArray()));
            ++rowNum;
        }
        return rows;
    }

    @NotNull
    private List<GridColumn> constructColumns(final RemoteResultSetMetaData metaData)
            throws SQLException, RemoteException {
        final List<GridColumn> columns = new LinkedList<>();

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
        final boolean isDataSetFile;
        if (editor instanceof EditorWindow) {
            isDataSetFile = ((EditorWindow) editor).getDelegate()
                                                   .getDocument()
                                                   .getText()
                                                   .startsWith("<dataset>");
        } else {
            isDataSetFile = editor.getDocument().getText().startsWith("<dataset>");
        }
        SmartPsiElementPointer<SqlSelectStatement> pointer = getNearestPointer(project, psiElement);
        final String selectedText = editor.getSelectionModel().getSelectedText();
        final boolean hasSelectedQuery = editor.getSelectionModel().hasSelection() && selectedText.trim().toUpperCase().startsWith("SELECT");
        return isDataSetFile && (hasSelectedQuery || pointer != null);
    }

    private SmartPsiElementPointer<SqlSelectStatement> getNearestPointer(final @NotNull Project project,
                                                                         final @NotNull PsiElement psiElement) {
        SmartPsiElementPointer<SqlSelectStatement> pointer = getStatementPointer(project, psiElement);
        if (pointer == null && psiElement.getPrevSibling() != null) {
            pointer = getStatementPointer(project, psiElement.getPrevSibling());
        }
        if (pointer == null && psiElement.getPrevSibling() != null) {
            final String prevText = psiElement.getPrevSibling().getText();
            if ((prevText.equals(";") || prevText.isEmpty()) && psiElement.getPrevSibling().getPrevSibling() != null) {
                pointer = getStatementPointer(project, psiElement.getPrevSibling().getPrevSibling());
            }
        }
        return pointer;
    }

    @Nullable
    private SmartPsiElementPointer<SqlSelectStatement> getStatementPointer(final @NotNull Project project,
                                                                           final @NotNull PsiElement psiElement) {
        final SqlSelectStatement sqlSelectStatement =
                PsiTreeUtil.getParentOfType(psiElement.getContainingFile().findElementAt(psiElement.getTextOffset()),
                                            SqlSelectStatement.class);
        SmartPsiElementPointer<SqlSelectStatement> pointer = null;
        if (sqlSelectStatement != null) {
            pointer = SmartPointerManager.getInstance(project)
                                         .createSmartPsiElementPointer(sqlSelectStatement);
        }
        return pointer;
    }

    private void showPopup(final Editor editor,
                           final MessageType messageType,
                           final String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JBPopupFactory.getInstance()
                              .createHtmlTextBalloonBuilder(message, messageType, null)
                              .setFadeoutTime(7500)
                              .createBalloon()
                              .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.atRight);
            }
        });
    }
}
