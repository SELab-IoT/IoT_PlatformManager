package selab.hanyang.ac.kr.platformmanager.database.model;

import javax.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int _id;

    @Column(name = "pep_category")
    private String pepCategory;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

}
