package selab.hanyang.ac.kr.platformmanager.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer>{
}
