package li.ktt.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSettingsPageTest {

    @Mock
    private Project project;

    @Mock
    private ProjectSettings projectSettings;

    @Test
    public void shouldReturnCorrectPluginName() {
        // when
        ProjectSettingsPage page = new ProjectSettingsPage(project);

        // then
        assertEquals("DbUnit Extractor", page.getDisplayName());
        assertEquals("DbUnit Extractor", page.getId());
    }

    @Test
    public void shouldNotBeModify() {
        // given
        ProjectSettingsPage page = preparePageWithMocks();
        // when // then
        assertEquals(false, page.isModified());
    }

    @Test
    public void shouldBeModifyIfIncludeSchemaHasChanged() {
        // given
        ProjectSettingsPage page = preparePageWithMocks();
        // when
        page.getIncludeSchema().setSelected(false);
        // then
        assertEquals(true, page.isModified());
    }

    @Test
    public void shouldBeModifyIfSkipNullsHasChanged() {
        // given
        ProjectSettingsPage page = preparePageWithMocks();
        // when
        page.getSkipNullValues().setSelected(true);
        // then
        assertEquals(true, page.isModified());
    }

    @Test
    public void shouldBeModifyIfSkipEmptyHasChanged() {
        // given
        ProjectSettingsPage page = preparePageWithMocks();
        // when
        page.getSkipEmptyValues().setSelected(false);
        // then
        assertEquals(true, page.isModified());
    }

    @Test
    public void shouldBeModifyIfExcludedColumnsHasChanged() {
        // given
        ProjectSettingsPage page = preparePageWithMocks();
        // when
        page.getExcludedColumns().setText("SomeNewVALUE");
        // then
        assertEquals(true, page.isModified());
    }

    @Test
    public void shouldReturnNullForUnusedElements() {
        // when
        ProjectSettingsPage page = new ProjectSettingsPage(project);

        // then
        assertEquals(null, page.getHelpTopic());
        assertEquals(null, page.enableSearch("ble"));
    }

    @Test
    public void shouldApplyConfiguration () throws ConfigurationException {
        // given
        ProjectSettingsPage page = preparePageWithMocks();

        // when
        page.getExcludedColumns().setText("newVALUE");
        page.getIncludeSchema().setSelected(false);
        page.getSkipNullValues().setSelected(true);
        page.apply();

        // then
        verify(projectSettings).setProperties(project, false, true, true, "newVALUE");
    }

    private ProjectSettingsPage preparePageWithMocks() {
        ExtractorProperties extractorProperties = new ExtractorProperties(true, false, true, "ble\\.value\n");
        when(projectSettings.getExtractorProperties(project)).thenReturn(extractorProperties);
        ProjectSettingsPage page = new ProjectSettingsPage(project, projectSettings);
        page.createComponent();
        return page;
    }
}
