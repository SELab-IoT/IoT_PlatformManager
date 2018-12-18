package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import selab.hanyang.ac.kr.platformmanager.database.model.User;

public interface UserRepository extends JpaRepository<User, String>{

    @Query("select count(*)>0 from User where user_id = :userId AND user_pw = :userPW")
    boolean checkLogin(@Param("userId")String userId, @Param("userPW")String userPW);

}
