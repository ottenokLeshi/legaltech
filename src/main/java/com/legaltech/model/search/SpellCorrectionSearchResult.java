package com.legaltech.model.search;

import java.util.List;

public class SpellCorrectionSearchResult {
    Spellcheck spellcheck;

}

class Spellcheck {
    List<Object> suggestions;
}

class SuggestionObject {
    int numFound;
    Suggestion suggestion;
}

class Suggestion {

}
