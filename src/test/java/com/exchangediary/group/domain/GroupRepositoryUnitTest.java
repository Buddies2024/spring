package com.exchangediary.group.domain;

import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GroupRepositoryUnitTest {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GroupRepository groupRepository;

    @Test
    void 내_그룹순서가_현재_그룹순서와_일치하는지_확인() {
        Group group = Group.of("버니즈", "code");
        Member member = Member.from(1L);
        member.joinGroup("nickname", "red", 1, GroupRole.GROUP_MEMBER, group);
        entityManager.persist(group);
        entityManager.persist(member);

        Optional<Long> groupId = groupRepository.findGroupIdCurrentOrderEqualsMemberOrder(member.getId());

        assertThat(groupId.isPresent()).isTrue();
        assertThat(groupId.get()).isEqualTo(group.getId());
    }

    @Test
    void 내_그룹순서가_현재_그룹순서와_일치_안함() {
        Group group = Group.of("버니즈", "code");
        Member member = Member.from(1L);
        member.joinGroup("nickname", "red", 2, GroupRole.GROUP_MEMBER, group);
        entityManager.persist(group);
        entityManager.persist(member);

        Optional<Long> groupId = groupRepository.findGroupIdCurrentOrderEqualsMemberOrder(member.getId());

        assertThat(groupId.isPresent()).isFalse();
    }
}
