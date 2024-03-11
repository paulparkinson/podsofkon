package podsofkon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import podsofkon.model.Account;
import podsofkon.model.Summary;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary, Long> {


//    List<Summary> findByEmail(String email);
//    Summary findByAccountId(long accountId);
//    Summary findByAuthKey(String authKey);
//    void deleteByEmail(String email);
}
