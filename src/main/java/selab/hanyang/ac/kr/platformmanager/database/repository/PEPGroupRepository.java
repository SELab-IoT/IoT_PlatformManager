package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;

import java.util.List;

public interface PEPGroupRepository extends JpaRepository<PEPGroup, Long>{
    List<PEPGroup> findByOwner(User user);

    @Query("select pg from PEPGroup pg join PEP p where p.pepId=:#{#pep.pepId} and pg.owner=:user")
    PEPGroup findByOwnerAndPEP(User user, PEP pep);
}
