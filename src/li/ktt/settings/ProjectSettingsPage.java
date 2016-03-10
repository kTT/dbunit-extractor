package li.ktt.settings;

import com.intellij.database.util.DbUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ProjectSettingsPage implements SearchableConfigurable, Configurable.NoScroll {
    private JCheckBox skipNullValues;
    private JCheckBox skipEmptyValues;
    private JCheckBox includeSchema;
    private JTextArea excludedColumns;
    private JPanel panel;
    private JLabel selectedDataSource;
    private JComboBox<String> selectedDataSourceValue;

    private final ProjectSettings projectSettings;
    private ExtractorProperties extractorProperties;

    private Project project;

    public ProjectSettingsPage(final Project project) {
        this(ProjectSettings.getInstance(project));
        this.project = project;
    }

    protected ProjectSettingsPage(final ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
        this.extractorProperties = this.projectSettings.getExtractorProperties();
    }

    @NotNull
    @Override
    public String getId() {
        return getDisplayName();
    }

    @Nullable
    @Override
    public Runnable enableSearch(final String s) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "DbUnit Extractor";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        includeSchema.setSelected(extractorProperties.isIncludeSchema());
        skipNullValues.setSelected(extractorProperties.isSkipNull());
        skipEmptyValues.setSelected(extractorProperties.isSkipEmpty());
        excludedColumns.setText(extractorProperties.getExcludeColumns());

        selectedDataSourceValue.addItem("");
        for (final String datasourceName : DbUtil.getExistingDataSourceNames(project)) {
            selectedDataSourceValue.addItem(datasourceName);

            if (datasourceName.equals(extractorProperties.getSelectedDataSourceName())) {
                selectedDataSourceValue.setSelectedIndex(selectedDataSourceValue.getItemCount() - 1);
            }
        }
        return panel;
    }

    @Override
    public boolean isModified() {
        return includeSchema.isSelected() != extractorProperties.isIncludeSchema()
                || skipNullValues.isSelected() != extractorProperties.isSkipNull()
                || skipEmptyValues.isSelected() != extractorProperties.isSkipEmpty()
                || !excludedColumns.getText().equals(extractorProperties.getExcludeColumns())
                || !selectedDataSourceValue.getSelectedItem().equals(extractorProperties.getSelectedDataSourceName());
    }

    @Override
    public void apply() throws ConfigurationException {
        ExcludedColumns excludeValidator = new ExcludedColumns(excludedColumns.getText());
        if (excludeValidator.isValid()) {
            this.extractorProperties = projectSettings.setProperties(
                    includeSchema.isSelected(),
                    skipNullValues.isSelected(),
                    skipEmptyValues.isSelected(),
                    excludedColumns.getText(),
                    (String) selectedDataSourceValue.getSelectedItem());
        } else {
            String message = invalidRegularExpressionsMessage(excludeValidator);
            throw new ConfigurationException(message);
        }
    }

    @NotNull
    private String invalidRegularExpressionsMessage(ExcludedColumns excludeValidator) {
        String lines = "Invalid regular expressions in lines: ";
        int size = excludeValidator.getInvalidLines().size();
        for (int i = 0; i < size; i++) {
            int lineNumber = excludeValidator.getInvalidLines().get(i);
            lines += lineNumber;
            if (i < size - 1) {
                lines += ", ";
            }
        }
        return lines;
    }

    @Override
    public void reset() {
        skipNullValues.setSelected(extractorProperties.isSkipNull());
        skipEmptyValues.setSelected(extractorProperties.isSkipEmpty());
        includeSchema.setSelected(extractorProperties.isIncludeSchema());
        excludedColumns.setText(extractorProperties.getExcludeColumns());
    }

    @Override
    public void disposeUIResources() {
    }

    protected JCheckBox getIncludeSchema() {
        return includeSchema;
    }

    protected JCheckBox getSkipNullValues() {
        return skipNullValues;
    }

    protected JCheckBox getSkipEmptyValues() {
        return skipEmptyValues;
    }

    protected JTextArea getExcludedColumns() {
        return excludedColumns;
    }

}
