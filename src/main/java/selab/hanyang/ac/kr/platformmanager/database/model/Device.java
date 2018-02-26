package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "device")
public class Device {

    public String getId() {
        return deviceId;
    }

    public String getName() {
        return deviceName;
    }

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @ManyToOne
    @JoinColumn(name = "pep_id")
    private PEP pep;

    public Device(){}

    public Device(String devID, String devName, PEP pep) {
        this.deviceId = devID;
        this.deviceName = devName;
        this.pep = pep;
    }
}
