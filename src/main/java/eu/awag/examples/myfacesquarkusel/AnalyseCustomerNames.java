package eu.awag.examples.myfacesquarkusel;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ViewScoped
@RolesAllowed("analyse-customer-names")
public class AnalyseCustomerNames implements Serializable {


    private int limitAnalyseCustomersCount = 100;
    private int minScore = 80;
    private int friendsCount = 1;
    private int totalCustomerCount;

    private List<Customer> customers;

    private String searchTerm;

    @Inject
    CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        totalCustomerCount = customerRepository.getCustomers().size();
    }

    public void actionRunAnalyze() {

        try {
            Path f = Files.createTempFile("customers", ".csv");
            try (var in = getClass().getClassLoader().getResourceAsStream("/customers.csv")) {
                Files.copy(in, f, StandardCopyOption.REPLACE_EXISTING);
            }

            customers =customerRepository.getCustomers();
            System.out.println(searchTerm);
            if(searchTerm!=null && !searchTerm.isBlank()) {
                customers = customers.stream().filter(e->e.name().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
            }

            var customersMap = customers.stream()
                    .collect(Collectors.groupingBy(e -> e.name(),Collectors.mapping(e1->e1,Collectors.toList())));

            customers = customers.stream()
                    .limit(limitAnalyseCustomersCount)
                    .map(customer -> searchFriends(customer, customersMap,customers))
                    .filter(customer -> customer.friends()!=null && !customer.friends().isEmpty())
                    .collect(Collectors.toList());
            if(friendsCount>1) {
                customers = customers.stream().filter(customer -> customer.friends().size()>1).collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load customer data", e);
        }
    }

    private Customer searchFriends(Customer customer, Map<String, List<Customer>> customersMap,List<Customer> customers) {
        return Customer.ofCustomerWithFriends(customer, FuzzySearch.extractTop(customer.name(), customers.stream()
                                .filter(c->!c.equals(customer))
                        .map(e -> e.name())
                        .collect(Collectors.toList()), 10)
                .stream().map(e -> getFriends(customersMap, e))
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    private List<Customer> getFriends(Map<String, List<Customer>> customersMap, ExtractedResult result) {
        return customersMap.get(result.getString()).stream()
                .map(customer -> Customer.ofScore(customer, result.getScore()))
                .filter(c-> c.score() >= minScore)
                .collect(Collectors.toList());
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getLimitAnalyseCustomersCount() {
        return limitAnalyseCustomersCount;
    }

    public void setLimitAnalyseCustomersCount(int limitAnalyseCustomersCount) {
        this.limitAnalyseCustomersCount = limitAnalyseCustomersCount;
    }

    public int getTotalCustomerCount() {
        return totalCustomerCount;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

}
