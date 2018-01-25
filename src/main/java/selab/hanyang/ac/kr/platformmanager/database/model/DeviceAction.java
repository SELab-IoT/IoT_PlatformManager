package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "device_action")
public class DeviceAction {

    public DeviceAction(String actionID, String actionName, Device device, String params){
        this.actionID = actionID;
        this.actionName = actionName;
        this.device = device;
        this.params = params;
    }

    @Id
    @Column(name = "action_id")
    private String actionID;

    @Column(name = "actionName")
    private String actionName;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "params")
    private String params;
}
