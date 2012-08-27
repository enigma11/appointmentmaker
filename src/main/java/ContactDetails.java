import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contact")
public class ContactDetails {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String telephone;

    public ContactDetails(String firstName, String lastName, String email, String telephone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }
}
