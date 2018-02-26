package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "pep")
public class PEP {

    public PEP() {
    }

    @Id
    @Column(name = "pep_id", nullable = false)
    public String pepId;

    @Column(name = "pep_name")
    public String pepName;

    @ManyToOne
    @JoinColumn(name = "pep_group_id")
    public PEPGroup pepGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pdp_id")
    public PDP pdp;

    @Column(name = "ip")
    public String ip;

    public String getId() {
        return pepId;
    }

    public PDP getPDP() {
        return pdp;
    }

    public PEPGroup getPepGroup() {
        return pepGroup;
    }

    public void setPepName(String pepName) {
        this.pepName = pepName;
    }

    public void setPepGroup(PEPGroup pepGroup) {
        this.pepGroup = pepGroup;
    }

    public void setPdp(PDP pdp) {
        this.pdp = pdp;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

}
