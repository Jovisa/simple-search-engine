package com.griddynamics.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.griddynamics.util.Constants.WORD_SEPARATOR;


public final class Repository {
    private final Map<String, List<Integer>> invertedIndex;
    private final List<String> listOfPeople;

    public Repository(String dataSourceFilePath) {
        this.listOfPeople = loadDataFromFile(dataSourceFilePath);
        this.invertedIndex = new HashMap<>();

        listOfPeople.forEach(personInfo -> Arrays
                .stream(personInfo.toLowerCase().split(WORD_SEPARATOR)) // tokenize each line into terms
                .toList()
                .forEach(term -> updateIndex(invertedIndex, term, listOfPeople.indexOf(personInfo))) // indexing
        );
    }

    private void updateIndex(Map<String, List<Integer>> index, String term, int posting) {
        List<Integer> postingsList = index.getOrDefault(term, new ArrayList<>());
        postingsList.add(posting);
        index.put(term, postingsList);
    }

    private List<String> loadDataFromFile(String filePath) {
        String rawData = readDataFromFileAsString(filePath);
        return  Arrays.stream(rawData.split("\n")).collect(Collectors.toList());
    }

    private  String readDataFromFileAsString(String fileName)  {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try(InputStream inputStream = classloader.getResourceAsStream(fileName)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> queryAND(List<String> terms) {
        return getNamesFromPostings(getAllPostingsOperationAND(terms));
    }

    public List<String> queryOR(List<String> terms) {
        return getNamesFromPostings(getAllPostingsOperationOR(terms));
    }

    private List<String> getNamesFromPostings(Set<Integer> postings) {
        return postings.stream()
                .map(listOfPeople::get)
                .toList();
    }

    public List<String> queryNONE(List<String> terms) {
        List<String> names = queryAND(terms);
        return listOfPeople.stream()
                .filter(person -> !names.contains(person))
                .toList();
    }

    public Set<Integer> getAllPostingsOperationAND(List<String> terms) {
        Set<Integer> postings = new HashSet<>();
        terms.forEach(term -> {
            if (postings.isEmpty()) {
                postings.addAll(getPostingsList(term));
            } else {
                postings.retainAll(getPostingsList(term));
            }
        });
        return postings;
    }

    public Set<Integer> getAllPostingsOperationOR(List<String> terms) {
        Set<Integer> postings = new HashSet<>();
        terms.forEach(
                term -> postings.addAll(getPostingsList(term))
        );
        return postings;
    }

    private List<Integer> getPostingsList(String term) {
        return invertedIndex.getOrDefault(term, new ArrayList<>());
    }

    public List<String> getAllData() {
        return this.listOfPeople;
    }
}
