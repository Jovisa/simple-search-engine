package com.griddynamics;

import com.griddynamics.model.SearchRequest;
import com.griddynamics.model.strategy.SearcherAll;
import com.griddynamics.model.strategy.SearcherAny;
import com.griddynamics.model.strategy.SearcherNone;
import com.griddynamics.repository.Repository;
import com.griddynamics.service.SearchService;
import org.junit.jupiter.api.Test;


import java.util.List;

import static com.griddynamics.util.Constants.DATA_FILE_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SearchServiceTest {
    private final List<String> NOT_FOUND = List.of("No matching people found.");

    private SearchRequest searchRequest;

    private final Repository repository = new Repository(DATA_FILE_PATH);

    private final SearchService searchService = new SearchService(repository);


    @Test
    public void getAllNamesTest() {
        assertThat( "11 Names should be found", searchService.geAllNames().size(), equalTo(11));
        assertThat( "1st persons name is Kristofer Galley", searchService.geAllNames().get(0), equalTo("Kristofer Galley"));
        assertThat( "second person is Fernando Marbury and he has email", searchService.geAllNames().get(1), equalTo("Fernando Marbury fernando_marbury@gmail.com"));
        assertThat( "10th person is Bob Yeh and he has email", searchService.geAllNames().get(9), equalTo("Bob Yeh bobyeah@gmail.com"));
    }

    /**
     * Search Strategy: ALL
     * only full name and surname or full email is a valid search phrase with this strategy
     * case-insensitive
     */
    @Test
    public void searchNameStrategyAllTest() {
        searchRequest = new SearchRequest("Malena Gray", new SearcherAll());
        assertThat( "Malena Gray should be found", searchService.search(searchRequest).get(0), equalTo("Malena Gray"));

        searchRequest = new SearchRequest("Bla", new SearcherAll());
        assertThat( "bla should be found", searchService.search(searchRequest).get(0), equalTo("Bla"));

        searchRequest.setPhrase("Kristyn Nix");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));

        searchRequest.setPhrase("Kristyn Nix nix-kris@gmail.com");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));


        searchRequest.setPhrase("Malena nix-kris@gmail.com");
        assertThat( "No result is found", searchService.search(searchRequest), equalTo(NOT_FOUND));

        searchRequest.setPhrase("Malena");
        assertThat( "Malena Gray should be found", searchService.search(searchRequest).get(0), equalTo("Malena Gray"));

        searchRequest.setPhrase("Nix Malena");
        assertThat( "No result is found", searchService.search(searchRequest), equalTo(NOT_FOUND));
    }

    /**
     * Search Strategy: ANY
     * only name, only surname or only mail as well as full name with or without email is valid search phrase
     * case-insensitive
     */
    @Test
    public void searchNameStrategyAnyTest() {
        searchRequest = new SearchRequest("Malena Gray", new SearcherAny());
        assertThat( "Malena Gray should be found", searchService.search(searchRequest).get(0), equalTo("Malena Gray"));

        searchRequest.setPhrase("Kristyn Nix");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));

        searchRequest.setPhrase("Kristyn Nix nix-kris@gmail.com");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));


        searchRequest.setPhrase("malena");
        assertThat( "Malena Gray should be found", searchService.search(searchRequest).get(0), equalTo("Malena Gray"));

        searchRequest.setPhrase("Kristyn");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));

        searchRequest.setPhrase("Nix");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));

        searchRequest.setPhrase("nix-kris@gmail.com");
        assertThat( "Kristyn Nix should be found", searchService.search(searchRequest).get(0), equalTo("Kristyn Nix nix-kris@gmail.com"));

    }

    /**
     * Search Strategy: NONE
     * same as ANY in terms of valid search phrases
     * returns all names that don't match search phrase ( like a negation of ANY )
     * case-insensitive
     */
    @Test
    public void searchNameStrategyNoneTest() {
        searchRequest = new SearchRequest("Malena Gray", new SearcherNone());

        assertThat( "9 names should be found",
                searchService.search(searchRequest).size(), equalTo(10));
        assertThat( "All names except Malena Grey should be found",
                searchService.search(searchRequest).contains("Malena Gray"), equalTo(false));

        searchRequest.setPhrase("Kristyn Nix");
        assertThat( "9 names should be found",
                searchService.search(searchRequest).size(), equalTo(10));
        assertThat( "All names except Kristyn Nix should be found",
                searchService.search(searchRequest).contains("Kristyn Nix"), equalTo(false));

        searchRequest.setPhrase("malena");
        assertThat( "9 names should be found",
                searchService.search(searchRequest).size(), equalTo(10));
        assertThat( "All names except Malena Grey should be found",
                searchService.search(searchRequest).contains("Malena Gray"), equalTo(false));

        searchRequest.setPhrase("kristyn");
        assertThat( "9 names should be found",
                searchService.search(searchRequest).size(), equalTo(10));
        assertThat( "All names except Kristyn Nix should be found",
                searchService.search(searchRequest).contains("Kristyn Nix"), equalTo(false));

        searchRequest.setPhrase("nix-kris@gmail.com");
        assertThat( "9 names should be found",
                searchService.search(searchRequest).size(), equalTo(10));
        assertThat( "All names except Kristyn Nix should be found",
                searchService.search(searchRequest).contains("Kristyn Nix"), equalTo(false));
    }

    @Test
    public void searchPhraseTooShortTest() {
        searchRequest = new SearchRequest("m", new SearcherAll());
        assertThat( "Phrase is too short, name can't be found",
                searchService.search(searchRequest).equals(NOT_FOUND), equalTo(true));
    }
}
