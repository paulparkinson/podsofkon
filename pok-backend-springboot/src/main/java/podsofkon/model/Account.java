package podsofkon.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ACCOUNTS")
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private long accountId;

    @Generated(GenerationTime.INSERT)
    @Column(name = "ACCOUNT_OPENED_DATE", updatable = false, insertable = false)
    private Date accountOpenedDate;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "STREET_ADDRESS")
    private String streetAddress;

    @Column(name = "STREET_ADDRESS2")
    private String streetAddress2;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIPCODE")
    private String zipcode;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "AUTH_KEY")
    private String authKey;

    @Column(name = "BALANCE")
    private BigDecimal balance;; //existence of authkey indicates registration was confirmed

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(name = "ACCOUNT_ID")
//    private Set<Usage> usages = new HashSet<>();
//
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(name = "ACCOUNT_ID")
//    private Set<AuthKey> authKeys = new HashSet<>();

    public Account(String firstName, String accountType, String email, String password) {
        this.firstName = firstName;
        this.lastName = accountType;
        this.email = email;
        this.password = password;
    }
}
