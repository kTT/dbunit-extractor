package li.ktt;

import com.intellij.openapi.project.Project;
import li.ktt.settings.ProjectSettings;

public class XmlConfiguration {

    private final boolean includeSchemaEnabled;

    private final boolean skipNullEnabled;

    private final boolean skipEmptyEnabled;

    public XmlConfiguration(boolean includeSchemaEnabled, boolean skipNullEnabled, boolean skipEmptyEnabled) {
        this.includeSchemaEnabled = includeSchemaEnabled;
        this.skipNullEnabled = skipNullEnabled;
        this.skipEmptyEnabled = skipEmptyEnabled;
    }

    public XmlConfiguration(Project project) {
        this.includeSchemaEnabled = ProjectSettings.isIncludeSchemaEnabled(project);
        this.skipNullEnabled = ProjectSettings.isSkipNullEnabled(project);
        this.skipEmptyEnabled = ProjectSettings.isSkipEmptyEnabled(project);

    }

    public boolean isIncludeSchemaEnabled() {
        return includeSchemaEnabled;
    }

    public boolean isSkipNullEnabled() {
        return skipNullEnabled;
    }

    public boolean isSkipEmptyEnabled() {
        return skipEmptyEnabled;
    }

}
