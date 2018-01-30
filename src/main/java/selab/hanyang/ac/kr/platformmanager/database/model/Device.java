package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "device")
public class Device {

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @ManyToOne
    @JoinColumn(name = "pep_id")
    private PEP pep;

    @Column(name = "profile")
    private String profile;

    private Device(){}

    public Device(String devID, String devName, PEP pep, String profile) {
        this.deviceId = devID;
        this.deviceName = devName;
        this.pep = pep;
        this.profile = profile;
    }
}
