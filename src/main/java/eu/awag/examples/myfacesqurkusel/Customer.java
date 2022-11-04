package eu.awag.examples.myfacesqurkusel;

import java.util.HashSet;
import java.util.List;

public record Customer(int id, String name, String customerNumber, List<Customer> friends, int score) {


    public static Customer ofRow(String[] row) {
        return new Customer(Integer.valueOf(row[0]),row[1],row[2]!=null && !row[2].equals("NULL") ? row[2] : null,null,0);
    }
    public static Customer ofScore(Customer customer, int score) {
        return new Customer(customer.id,customer.name,customer.customerNumber,null,score);
    }
    public static Customer ofCustomerWithFriends(Customer customer, List<Customer> friends) {
        return new Customer(customer.id,customer.name,customer.customerNumber, new HashSet<>(friends).stream().toList(),0);
    }
}
