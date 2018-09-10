package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "device_action")
public class DeviceAction {

    private DeviceAction(){}

    public DeviceAction(String actionId, String actionName, Device device, String params){
        this.actionId = actionId;
        this.actionName = actionName;
        this.device = device;
        this.params = params;
    }

    @Id
    @Column(name = "action_id")
    private String actionId;

    @Column(name = "action_name")
    private String actionName;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "params")
    private String params;

    public String getActionId() {
        return actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public Device getDevice() {
        return device;
    }

    public String getParams() {
        return params;
    }
}
