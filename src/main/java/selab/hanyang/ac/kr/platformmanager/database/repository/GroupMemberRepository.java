package selab.hanyang.ac.kr.platformmanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selab.hanyang.ac.kr.platformmanager.database.model.GroupMember;
import selab.hanyang.ac.kr.platformmanager.database.model.GroupMemberId;
import selab.hanyang.ac.kr.platformmanager.database.model.PEPGroup;
import selab.hanyang.ac.kr.platformmanager.database.model.User;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId>{
    List<GroupMember> findByPepGroup(PEPGroup pepGroup);
    List<GroupMember> findByUserId(User userId);
}
