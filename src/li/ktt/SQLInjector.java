package li.ktt;

import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class SQLInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull final PsiLanguageInjectionHost host,
                                     @NotNull final InjectedLanguagePlaces places) {
        final boolean isSelectQuery = host.getText().trim().toUpperCase().startsWith("SELECT");
        final boolean isDataSetFile = host.getContainingFile().getText().startsWith("<dataset>");
        if (isDataSetFile && isSelectQuery) {
            final Language language = Language.findLanguageByID("SQL");
            if (language != null) {
                places.addPlace(language, TextRange.from(0, host.getTextLength()), null, null);
            }
        }
    }
}
