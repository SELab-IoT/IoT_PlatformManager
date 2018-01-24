package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selab.hanyang.ac.kr.platformmanager.database.model.PDP;

import java.util.List;

public interface PDPRepository extends JpaRepository<PDP, Integer> {

    @Query("select p.name from PDP p")
    List<String> findAllName();

}
