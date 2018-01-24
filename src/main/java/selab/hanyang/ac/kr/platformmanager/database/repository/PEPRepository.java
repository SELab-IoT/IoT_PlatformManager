package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;

public interface PEPRepository extends JpaRepository<PEP, String> {


    PEP findOneByPepId(String pepId);
}
