package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "attribute")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int _id;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @Column(name = "pep_attribute")
    private String pepAttribute;

    @Column(name = "attribute_id", nullable = false)
    private String attributeId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

}
