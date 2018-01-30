package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.Device;
import selab.hanyang.ac.kr.platformmanager.database.model.DeviceAction;

import java.util.List;

public interface DeviceActionRepository extends JpaRepository<DeviceAction, String> {
    List<DeviceAction> findByDeviceId(Device deviceId);
}
