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

    public String getMacAddress() {return macAddress; }

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "mac_address")
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "pep_id")
    private PEP pep;

    public Device(){}

    public Device(String deviceId, String deviceName, String macAddress, PEP pep) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.pep = pep;
    }
}
