package li.ktt.settings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExcludedColumnsTest {

    private static final String VALID_EXCLUDED_LIST = "ha!\n"
            + "someValidValue\n"
            + ".*\\.dbo\n"
            + "tralala[A-Z]\n" +
            "((haa!))";

    @Test
    public void shouldReturnOnlyValidPattern() {
        // given
        String excludedString = "\\\n"
                + "someValidValue\n"
                + ".*\\.dbo\n"
                + "tralla[\n" +
                "())";

        // when
        ExcludedColumns excludedColumns = new ExcludedColumns(excludedString);

        // then
        assertEquals(false, excludedColumns.isValid());
        assertEquals(2, excludedColumns.getPatterns().size());
        assertEquals(3, excludedColumns.getInvalidLines().size());
        assertEquals(true, excludedColumns.getInvalidLines().contains(1));
        assertEquals(true, excludedColumns.getInvalidLines().contains(4));
        assertEquals(true, excludedColumns.getInvalidLines().contains(5));
    }

    @Test
    public void shouldBeValid() {
        // when
        ExcludedColumns excludedColumns = new ExcludedColumns(VALID_EXCLUDED_LIST);

        // then
        assertEquals(true, excludedColumns.isValid());
        assertEquals(5, excludedColumns.getPatterns().size());
        assertEquals(0, excludedColumns.getInvalidLines().size());
    }

    @Test
    public void shouldBeAdded() {
        // when
        ExcludedColumns excludedColumns = new ExcludedColumns(VALID_EXCLUDED_LIST);

        // then
        String[] shouldBeAddedValues = new String[]{"hej", "dbo.Proudcts", "dbo.Kojoty", "tralala1"};
        for (String value : shouldBeAddedValues) {
            assertEquals(true, excludedColumns.canBeAdded(value));
        }
    }

    @Test
    public void shouldNotBeAdded() {
        // when
        ExcludedColumns excludedColumns = new ExcludedColumns(VALID_EXCLUDED_LIST);

        // then
        String[] shouldNotBeAddedValues = new String[]{ "tralalaZ", "tralalaA", "someValidValue"};
        for (String value : shouldNotBeAddedValues) {
            assertEquals(false, excludedColumns.canBeAdded(value));
        }
    }
}
