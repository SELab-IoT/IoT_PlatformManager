package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.DeviceAction;

public interface DeviceActionRepository extends JpaRepository<DeviceAction, String> {

}
