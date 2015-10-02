package li.ktt.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

public class ProjectSettings {
    private static final String DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY = "DbUnitExtractorSkipNull";
    private static final String DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY = "DbUnitExtractorSkipEmpty";
    private static final String DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY = "DbUnitExtractorIncludeSchema";
    private static final String DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY = "DbUnitExtractorExcludeColumns";

    private static final Key<DbUnitProperties> DB_UNIT_PROPERTIES_KEY = Key.create(
            "DbUnitProperties");

    public static void setProperties(final Project project,
                                     Boolean skipNull,
                                     Boolean skipEmpty,
                                     Boolean includeSchema,
                                     String excludeColumns) {
        final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);

        propertiesComponent.setValue(DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY, String.valueOf(skipNull));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY,
                                     String.valueOf(skipEmpty));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY,
                                     String.valueOf(includeSchema));
        propertiesComponent.setValue(DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY, excludeColumns);

        DbUnitProperties dbUnitProperties = new DbUnitProperties();
        dbUnitProperties.setSkipNull(skipNull);
        dbUnitProperties.setSkipEmpty(skipEmpty);
        dbUnitProperties.setIncludeSchema(includeSchema);
        dbUnitProperties.setExcludeColumns(excludeColumns);

        project.putUserData(DB_UNIT_PROPERTIES_KEY, dbUnitProperties);
    }

    public static boolean isSkipNullEnabled(final Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        return propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_SKIP_NULL_PROPERTY, true);
    }

    public static boolean isSkipEmptyEnabled(final Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        return propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_SKIP_EMPTY_PROPERTY, true);
    }

    public static boolean isIncludeSchemaEnabled(final Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        return propertiesComponent.getBoolean(DBUNIT_EXTRACTOR_INCLUDE_SCHEMA_PROPERTY, false);
    }

    public static String getExcludeColumns(final Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        return propertiesComponent.getValue(DBUNIT_EXTRACTOR_EXCLUDE_COLUMNS_PROPERTY, "");
    }
}
