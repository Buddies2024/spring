package com.exchangediary.group.domain;

import com.exchangediary.group.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByCode(String code);
    @Query("SELECT g.id FROM Group g JOIN g.members m ON m.id = :memberId WHERE m.orderInGroup = g.currentOrder")
    Optional<Long> findGroupIdCurrentOrderEqualsMemberOrder(Long memberId);
}
