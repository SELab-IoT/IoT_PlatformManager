package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.Device;
import selab.hanyang.ac.kr.platformmanager.database.model.PEP;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByPep(PEP pep);
}
