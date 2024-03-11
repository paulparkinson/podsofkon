package podsofkon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import podsofkon.model.Account;

import java.util.List;


public interface AccountRepository extends JpaRepository <Account, Long> {
//    List<Account> findAccountsByAccountNameContains (String accountName);

    List<Account> findByEmail(String email);
    Account findByAccountId(long accountId);
    Account findByAuthKey(String authKey);
    void deleteByEmail(String email);
}
