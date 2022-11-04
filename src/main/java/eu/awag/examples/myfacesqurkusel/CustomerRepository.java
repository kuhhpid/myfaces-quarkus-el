package eu.awag.examples.myfacesqurkusel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerRepository {

    private List<Customer> customers;

    public List<Customer> getCustomers() {
        return customers;
    }

    @PostConstruct
    public void init() {
        customers = """
                    id;name;customerNumber
                    1;Planus GmbH;NULL
                    2;R-werk Planung e.V;NULL
                    3;Sussane Haag Haus Hagen;NULL
                    4;Stadt hansen;NULL
                    5;HantelnWirtschaft e.V.;NULL
                    6;Zirkus Hostel;NULL
                    7;E+SSR;D10099
                    8;Brüwag;D17036
                    9;ATA;D17432
                    10;energy-consulting-peters;NULL
                    11;F.A. Südhoffen;NULL
                    12;Peter Hotel GmbH & Co. KG;D12234
                    13;Hotel Hegen Peters;NULL
                    14;NaaConrad GmbH;D33445
                    15;Senioren- und Pflegeheim St. Gallen;D3356
                    """.lines().skip(1)
                .map(e -> e.split(";"))
                .map(Customer::ofRow).collect(Collectors.toList());
    }
}
