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

    public Device(String deviceId, String deviceName, PEP pep) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.pep = pep;
    }
}
