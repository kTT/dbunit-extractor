package li.ktt.settings;

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

    final private Project project;

    public ProjectSettingsPage(final Project project) {
        this.project = project;
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
        ExtractorProperties extractorProperties = ProjectSettings.getExtractorProperties(project);

        includeSchema.setSelected(extractorProperties.isIncludeSchema());
        skipNullValues.setSelected(extractorProperties.isSkipNull());
        skipEmptyValues.setSelected(extractorProperties.isSkipEmpty());
        excludedColumns.setText(extractorProperties.getExcludeColumns());

        return panel;
    }

    @Override
    public boolean isModified() {
        ExtractorProperties extractorProperties = ProjectSettings.getExtractorProperties(project);

        return skipNullValues.isSelected() != extractorProperties.isSkipNull()
                || skipEmptyValues.isSelected() != extractorProperties.isSkipEmpty()
                || includeSchema.isSelected() != extractorProperties.isIncludeSchema()
                || !excludedColumns.getText().equals(extractorProperties.getExcludeColumns());
    }

    @Override
    public void apply() throws ConfigurationException {
        ProjectSettings.setProperties(project,
                skipNullValues.isSelected(),
                skipEmptyValues.isSelected(),
                includeSchema.isSelected(),
                excludedColumns.getText());
    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }

}
