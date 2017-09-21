package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;

public interface PEPRepository extends JpaRepository<PEP, Integer> {


    PEP findOneByPepId(String pepId);
}
