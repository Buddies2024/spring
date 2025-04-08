package com.exchangediary.group.domain;

import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GroupMemberRepositoryUnitTest {
    private static final String GROUP_NAME = "버디즈";
    private static final String[] PROFILE_IMAGES = {"red", "orange", "yellow", "green", "blue", "navy", "purple"};
    private static final String NICKNAME = "스프링";
    
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    private Member member;
    private Group group;

    @BeforeEach
    void setup() {
        Member member = Member.from(1L);
        entityManager.persist(member);
        this.member = member;

        Group group = Group.from(GROUP_NAME);
        entityManager.persist(group);
        this.group = group;
    }

    @Test
    @DisplayName("사용자 id로 그룹원 가져오기")
    void Test_findByMemberId() {
        // Given
        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);

        entityManager.flush();
        entityManager.clear();

        // When
        GroupMember result = groupMemberRepository.findByMemberId(member.getId()).get();

        // Then
        assertThat(result.getId()).isEqualTo(groupMember.getId());
        assertThat(result.getGroup().getId()).isEqualTo(group.getId());
        assertThat(result.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("사용자 id로 그룹원의 그룹 id 가져오기")
    void Test_findGroupIdByMemberId() {
        // Given
        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);

        entityManager.flush();
        entityManager.clear();

        // When
        String groupId = groupMemberRepository.findGroupIdByMemberId(member.getId()).get();

        // Then
        assertThat(groupId).isEqualTo(group.getId());
    }

    @Test
    @DisplayName("사용자 id로 그룹원의 마지막으로 조회 가능한 일기 날짜 가져오기")
    void Test_findLastViewableDiaryDateByMemberId() {
        // Given
        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);

        entityManager.flush();
        entityManager.clear();

        // When
        LocalDate lastViewableDiaryDate = groupMemberRepository.findLastViewableDiaryDateByMemberId(member.getId()).get();

        // Then
        assertThat(lastViewableDiaryDate).isEqualTo(groupMember.getLastViewableDiaryDate());
    }

    @Test
    @DisplayName("사용자 id로 그룹원의 리더 여부 판별")
    void Test_isGroupLeaderByMemberId() {
        // Member is group leader
        // Given
        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);

        entityManager.flush();
        entityManager.clear();

        // When
        boolean isLeader = groupMemberRepository.isGroupLeaderByMemberId(member.getId());

        // Then
        assertThat(isLeader).isTrue();

        // Member is not group leader
        // Given
        groupMember = groupMemberRepository.findByMemberId(member.getId()).get();
        groupMember.changeGroupRole(GroupRole.GROUP_MEMBER);

        entityManager.flush();
        entityManager.clear();

        // When
        isLeader = groupMemberRepository.isGroupLeaderByMemberId(member.getId());

        // Then
        assertThat(isLeader).isFalse();
    }

    @Test
    @DisplayName("사용자 id로 그룹원이 현재 그룹 순서인지 판별하기")
    void Test_isCurrentOrderByMemberId() {
        // Member`s order is current
        // Given
        GroupMember groupMember = GroupMember.of(NICKNAME, PROFILE_IMAGES[0], 1, GroupRole.GROUP_LEADER, group, member);
        entityManager.persist(groupMember);

        entityManager.flush();
        entityManager.clear();

        // When
        boolean isCurrentOrder = groupMemberRepository.isCurrentOrderByMemberId(member.getId());

        // Then
        assertThat(isCurrentOrder).isTrue();

        // Member`s order is not current
        // Given
        groupMember = groupMemberRepository.findByMemberId(member.getId()).get();
        groupMember.changeOrderInGroup(2);

        entityManager.flush();
        entityManager.clear();

        // When
        isCurrentOrder = groupMemberRepository.isCurrentOrderByMemberId(member.getId());

        // Then
        assertThat(isCurrentOrder).isFalse();
    }
}
