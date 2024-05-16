package bankManagerFlow;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Customer {

    private WebDriver webDriver;
    public String firstName;
    public String lastName;
    public String postCode;
    public Integer customerId;
    public List<Accounts> accounts;

    public Customer(){
        this.accounts = new ArrayList<>();
    }

}
