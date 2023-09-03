package com.griddynamics.service;


import com.griddynamics.repository.Repository;
import com.griddynamics.model.SearchRequest;
import com.griddynamics.model.strategy.Searcher;

import java.util.Arrays;
import java.util.List;

import static com.griddynamics.util.Constants.NOT_FOUND;
import static com.griddynamics.util.Constants.WORD_SEPARATOR;


public class SearchService {
    private final Repository repository;

    public SearchService(Repository repository) {
        this.repository = repository;
    }

    public List<String> search(SearchRequest searchRequest) {
        if (isPhraseInvalid(searchRequest.getPhrase())) {
            return List.of(NOT_FOUND);
        }

        List<String> result = executeSearch(searchRequest);
        return result.isEmpty()
                ? List.of(NOT_FOUND)
                : result;
    }

    public List<String> geAllNames() {
        return repository.getAllData();
    }

    private static boolean isPhraseInvalid(String phrase) {
        return phrase.length() < 2;
    }

    private List<String> executeSearch(SearchRequest searchRequest) {
        Searcher searcher = searchRequest.getSearcher();
        String phrase = searchRequest.getPhrase();

        List<String> tokenizedTerms = tokenize(phrase);
        return searcher.search(tokenizedTerms, repository);
    }

    private static List<String> tokenize(String phrase) {
        return Arrays.asList(phrase
                .toLowerCase()
                .split(WORD_SEPARATOR));
    }

}
