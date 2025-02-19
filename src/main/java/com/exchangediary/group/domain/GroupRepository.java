package com.exchangediary.group.domain;

import com.exchangediary.group.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, String> {
    @Query("SELECT CASE WHEN m.orderInGroup = g.currentOrder THEN true ELSE false END " +
            "FROM Group g JOIN g.members m ON m.id = :memberId")
    Boolean isEqualsToGroupCurrentOrder(Long memberId);
}
