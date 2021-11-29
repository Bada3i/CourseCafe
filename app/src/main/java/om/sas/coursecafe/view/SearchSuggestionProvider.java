package om.sas.coursecafe.view;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {


    public final static String AUTHORITY = "om.sas.coursecafe.view.SearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }


}
