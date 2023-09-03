package com.griddynamics.model.strategy;


import com.griddynamics.repository.Repository;

import java.util.List;


public interface Searcher {
    List<String> search(List<String> terms, Repository repository);
}
