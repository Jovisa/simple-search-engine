package com.griddynamics.model.strategy;

import com.griddynamics.repository.Repository;

import java.util.List;

public class SearcherAll implements Searcher {
    @Override
    public List<String> search(List<String> terms, Repository repository) {
        return repository.queryAND(terms);
    }
}
