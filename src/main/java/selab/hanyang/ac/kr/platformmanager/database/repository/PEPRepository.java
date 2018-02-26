package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;

import java.util.List;

public interface PEPRepository extends JpaRepository<PEP, String> {
    PEP findOneByPepId(String pepId);

    @Query("select p from PEP p where pep_group_Id = :pepGroupId")
    List<PEP> findByGroupId(@Param("pepGroupId")long pepGroupId);
}
