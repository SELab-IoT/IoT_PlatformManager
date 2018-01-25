package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;

import java.util.List;

public interface PEPGroupRepository extends JpaRepository<PEPGroup, String> {
    List<PEPGroup> findByOwner(String owner);
}
