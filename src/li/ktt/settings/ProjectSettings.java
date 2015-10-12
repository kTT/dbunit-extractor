package li.ktt.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

public class ProjectSettings {

    private static final String DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY = "DbUnitExtractorSkipNull";

    private static final String DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY = "DbUnitExtractorSkipEmpty";

    private static final String DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY = "DbUnitExtractorIncludeSchema";

    private static final String DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY = "DbUnitExtractorExcludeColumns";

    private static final Key<ExtractorProperties> DB_UNIT_PROPERTIES_KEY = Key.create(
            "DbUnitProperties");

    public static void setProperties(final Project project, Boolean skipNull, Boolean skipEmpty,
                                     Boolean includeSchema, String excludeColumns) {
        final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);

        propertiesComponent.setValue(DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY, String.valueOf(skipNull));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY,
                String.valueOf(skipEmpty));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY,
                String.valueOf(includeSchema));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY, excludeColumns);

        ExtractorProperties dbUnitProperties = new ExtractorProperties();
        dbUnitProperties.setSkipNull(skipNull);
        dbUnitProperties.setSkipEmpty(skipEmpty);
        dbUnitProperties.setIncludeSchema(includeSchema);
        dbUnitProperties.setExcludeColumns(excludeColumns);

        project.putUserData(DB_UNIT_PROPERTIES_KEY, dbUnitProperties);
    }

    public static ExtractorProperties getExtractorProperties(DataContext dataContext) {
        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        return getExtractorProperties(project);
    }

    public static ExtractorProperties getExtractorProperties(final Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        boolean includeSchema = propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY, false);
        boolean skipNull = propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY, true);
        boolean skipEmpty = propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY, true);
        String excludedColumns = propertiesComponent.getValue(DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY, "");
        return new ExtractorProperties(includeSchema, skipNull, skipEmpty, excludedColumns);
    }

}
