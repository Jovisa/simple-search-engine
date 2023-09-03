package com.griddynamics.model;


import com.griddynamics.model.strategy.Searcher;


public class SearchRequest {
    private String phrase;
    private Searcher searcher;

    public SearchRequest(String phrase, Searcher searcher) {
        this.phrase = phrase;
        this.searcher = searcher;
    }

    public SearchRequest() {
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }
}
