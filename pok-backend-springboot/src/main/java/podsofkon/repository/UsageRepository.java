package podsofkon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import podsofkon.model.Account;
import podsofkon.model.Usage;
import podsofkon.model.UsageId;

import java.util.List;

public interface UsageRepository extends JpaRepository<Usage, UsageId> {

    List<Usage> findByUsageId_Email(String email);
}
