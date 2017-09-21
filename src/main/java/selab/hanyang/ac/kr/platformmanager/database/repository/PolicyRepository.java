package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selab.hanyang.ac.kr.platformmanager.database.model.Policy;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Integer> {

    @Query("select p.name from Policy p")
    List<String> findAllName();

}
