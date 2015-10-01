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
        skipNullValues.setSelected(ProjectSettings.isSkipNullEnabled(project));
        skipEmptyValues.setSelected(ProjectSettings.isSkipEmptyEnabled(project));
        excludedColumns.setText(ProjectSettings.getExcludeColumns(project));
        return panel;
    }

    @Override
    public boolean isModified() {
        return skipNullValues.isSelected() != ProjectSettings.isSkipNullEnabled(project)
                || skipEmptyValues.isSelected() != ProjectSettings.isSkipEmptyEnabled(project)
                || !excludedColumns.getText().equals(ProjectSettings.getExcludeColumns(project));
    }

    @Override
    public void apply() throws ConfigurationException {
        ProjectSettings.setProperties(project,
                                      skipNullValues.isSelected(),
                                      skipEmptyValues.isSelected(),
                                      excludedColumns.getText());
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
