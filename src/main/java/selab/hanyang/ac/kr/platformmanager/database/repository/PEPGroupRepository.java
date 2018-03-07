package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;

import java.util.List;

public interface PEPGroupRepository extends JpaRepository<PEPGroup, Long>{

    @Query("select pg from PEP p join p.pepGroup pg where p.pepId=:#{#pep.pepId} and pg.owner.userId=:#{#owner.userId}")
    PEPGroup findByOwnerAndPEP(@Param("owner")User owner, @Param("pep")PEP pep);

    @Query("SELECT pg FROM GroupMember gm JOIN gm.pepGroup pg WHERE user_Id = ?#{[0]}")
    List<PEPGroup> findPEPGroupsByUserId(String userId);

    List<PEPGroup> findPEPGroupsByOwner(User owner);

    List<PEPGroup> findByOwner_UserId(String ownerId);

}
