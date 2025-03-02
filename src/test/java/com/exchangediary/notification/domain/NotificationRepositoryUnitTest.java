package com.exchangediary.notification.domain;

import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.diary.domain.entity.DiaryContent;
import com.exchangediary.group.domain.entity.Group;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.group.domain.enums.GroupRole;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.notification.domain.entity.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class NotificationRepositoryUnitTest {
    private static final String GROUP_NAME = "버디즈";
    private static final String[] PROFILE_IMAGES = {"red", "orange", "yellow", "green", "blue", "navy", "purple"};

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("그룹 안에서 member id를 제외한 모든 그룹원 토큰 가져오기")
    void Test_findTokensByGroupIdAndExcludeMemberId() {
        // Given
        Group group = Group.from("버니즈");
        entityManager.persist(group);

        Member self = setUpMember(1, group);
        for (int idx = 2; idx <= 4; idx++) {
            setUpMember(idx, group);
        }
        entityManager.flush();

        // When
        List<String> tokens = notificationRepository.findTokensByGroupIdAndExcludeMemberId(group.getId(), self.getId());

        // Then
        assertThat(tokens).hasSize(3);
    }

    @Test
    @DisplayName("사용자가 리더인 경우, 그룹 안에서 member id와 리더를 제외한 모든 그룹원 토큰 가져오기")
    void Test_findTokensByGroupIdAndExcludeMemberIdAndLeader_When_SelfIsLeader() {
        // Given
        Group group = Group.from(GROUP_NAME);
        entityManager.persist(group);

        Member self = setUpMember(1, group);
        for (int idx = 2; idx <= 4; idx++) {
            setUpMember(idx, group);
        }
        entityManager.flush();

        // When
        List<String> tokens = notificationRepository.findTokensByGroupIdAndExcludeMemberIdAndLeader(group.getId(), self.getId());

        // Then
        assertThat(tokens).hasSize(3);
    }

    @Test
    @DisplayName("사용자가 리더가 아닌 경우, 그룹 안에서 member id와 리더를 제외한 모든 그룹원 토큰 가져오기")
    void Test_findTokensByGroupIdAndExcludeMemberIdAndLeader_When_SelfIsNotLeader() {
        // Given
        Group group = Group.from(GROUP_NAME);
        entityManager.persist(group);

        Member Leader = setUpMember(1, group);
        Member self = setUpMember(2, group);
        for (int idx = 3; idx <= 4; idx++) {
            setUpMember(idx, group);
        }
        entityManager.flush();

        // When
        List<String> tokens = notificationRepository.findTokensByGroupIdAndExcludeMemberIdAndLeader(group.getId(), self.getId());

        // Then
        assertThat(tokens).hasSize(2);
    }

    @Test
    @DisplayName("현재 일기 작성 차례인 사용자 토큰 가져오기")
    void Test_findTokensByGroupIdAndCurrentOrder() {
        // Given
        Group group = Group.from(GROUP_NAME);
        entityManager.persist(group);

        Member member = setUpMember(1, group);
        for (int idx = 2; idx <= 4; idx++) {
            setUpMember(idx, group);
        }
        entityManager.flush();

        List<String> tokens = notificationRepository.findTokensByGroupIdAndCurrentOrder(group.getId());

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0)).isEqualTo(member.toString());
    }

    @Test
    @DisplayName("오늘 일기 작성하지 않은 그룹 내 모든 사용자 토큰 가져오기")
    void Test_findTokensByMembersWithoutDiaryToday_When_OnlyWriteGroup1() {
        // Given
        List<GroupMember> groupMembers = new ArrayList<>();

        for (int idx = 0; idx < 3; idx++) {
            Group group = Group.from(GROUP_NAME + idx);
            entityManager.persist(group);

            Member member = createMember(idx + 1);
            GroupMember groupMember = createGroupMember(1, group, member);
            createNotification(groupMember.toString(), member);

            if (idx == 0) {
                createDiary(groupMember, group);
            }
            groupMembers.add(groupMember);
        }

        // When
        List<String> tokens = notificationRepository.findTokensByMembersWithoutDiaryToday();

        // Then
        assertThat(tokens).hasSize(2);
        assertThat(tokens.contains(groupMembers.get(0).toString())).isFalse();
        assertThat(tokens.contains(groupMembers.get(1).toString())).isTrue();
        assertThat(tokens.contains(groupMembers.get(2).toString())).isTrue();
    }
    private Member setUpMember(int orderInGroup, Group group) {
        Member member = createMember(orderInGroup + 1);
        createGroupMember(orderInGroup, group, member);
        createNotification(member.toString(), member);
        return member;
    }

    private Member createMember(long kakaoId) {
        Member member = Member.from(kakaoId);
        entityManager.persist(member);
        return member;
    }

    private GroupMember createGroupMember(int orderInGroup, Group group, Member member) {
        GroupMember groupMember = GroupMember.of(
                PROFILE_IMAGES[orderInGroup],
                PROFILE_IMAGES[orderInGroup],
                orderInGroup,
                orderInGroup == 1 ? GroupRole.GROUP_LEADER : GroupRole.GROUP_MEMBER,
                group,
                member
        );
        entityManager.persist(groupMember);
        return groupMember;
    }

    private Notification createNotification(String token, Member member) {
        Notification notification = Notification.builder()
                .token(token)
                .member(member)
                .build();
        entityManager.persist(notification);
        return notification;
    }

    private Diary createDiary(GroupMember groupMember, Group group) {
        Diary diary = Diary.builder()
                .todayMood("happy")
                .groupMember(groupMember)
                .group(group)
                .build();
        entityManager.persist(diary);

        DiaryContent diaryContent = DiaryContent.of(1, groupMember.getNickname(), diary);
        entityManager.persist(diaryContent);

        return diary;
    }
}
