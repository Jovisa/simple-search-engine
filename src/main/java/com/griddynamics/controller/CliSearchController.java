package com.griddynamics.controller;

import com.griddynamics.model.SearchRequest;
import com.griddynamics.model.strategy.SearcherAll;
import com.griddynamics.model.strategy.SearcherAny;
import com.griddynamics.model.strategy.SearcherNone;
import com.griddynamics.repository.Repository;
import com.griddynamics.service.SearchService;

import java.util.List;
import java.util.Scanner;

import static com.griddynamics.util.Constants.*;

public class CliSearchController {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Scanner intScanner = new Scanner(System.in);
    private static boolean IS_RUNNING = true;

    private static final Repository repository = new Repository(DATA_FILE_PATH);
    private static final SearchService searchService = new SearchService(repository);

    public void run() {
        while (IS_RUNNING) {
            loadUserMenu();
            int option = intScanner.nextInt();
            switch (option) {
                case 1 -> search();
                case 2 -> printAllNames();
                case 0 -> exit();
                default -> System.out.println(INCORRECT_OPTION);
            }
        }
    }

    private void loadUserMenu() {
        System.out.println("""

                === Menu ===
                1. Find a person
                2. Print all people
                0. Exit""");
    }

    private void printAllNames() {
        System.out.println("=== List of people ===");
        searchService.geAllNames()
                .forEach(System.out::println);
    }

    private void exit() {
        System.out.println("\n Bye!");
        IS_RUNNING = false;
    }

    private void search() {
        SearchRequest request = new SearchRequest();

        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine();

        switch (strategy) {
            case ANY -> request.setSearcher(new SearcherAny());
            case NONE -> request.setSearcher(new SearcherNone());
            case ALL -> request.setSearcher(new SearcherAll());
        }

        System.out.println("Enter a name or email to search all suitable people:");
        request.setPhrase(scanner.nextLine());

        List<String> response = searchService.search(request);
        response.forEach(System.out::println);
    }
}
