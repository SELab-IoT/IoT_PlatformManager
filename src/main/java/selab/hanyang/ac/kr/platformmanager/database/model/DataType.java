package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "attribute_data_type")
public class DataType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int _id;

    @Column(name = "pep_type")
    private String pepType;

    @Column(name = "type")
    private String type;

}
