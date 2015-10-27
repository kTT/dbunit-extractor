package li.ktt.settings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ExcludedColumns {

    private final String excludedString;

    private final List<Pattern> patterns;

    private final List<Integer> invalidLines = new ArrayList<>();

    public ExcludedColumns(String excludedString) {
        this.excludedString = excludedString;
        this.patterns = initPatterns(this.excludedString);
    }

    public boolean isValid() {
        return this.invalidLines.isEmpty();
    }
    public List<Integer> getInvalidLines() {
        return invalidLines;
    }

    public boolean canBeAdded(String fullColumnName) {
        boolean result = true;
        for (final Pattern pattern : this.patterns) {
            if (pattern.matcher(fullColumnName).matches()) {
                result = false;
                break;
            }
        }
        return result;
    }

    protected List<Pattern> getPatterns() {
        return this.patterns;
    }

    private List<Pattern> initPatterns(String excludedColumns) {
        List<Pattern> patterns = new LinkedList<Pattern>();
        if (excludedColumns != null && !excludedColumns.isEmpty()) {
            String[] lines = excludedColumns.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                try {
                    Pattern pattern = Pattern.compile(line);
                    patterns.add(pattern);
                } catch (PatternSyntaxException pse) {
                    invalidLines.add(i + 1);
                }
            }
        }
        return patterns;
    }

}
