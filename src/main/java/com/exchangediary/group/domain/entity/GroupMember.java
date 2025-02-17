package com.exchangediary.group.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.member.domain.entity.Member;
import com.exchangediary.member.domain.enums.GroupRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED, force = true)
@AllArgsConstructor(access = PRIVATE)
public class GroupMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nickname;
    @NotNull
    private String profileImage;
    @NotNull
    private Integer orderInGroup;
    @NotNull
    private LocalDate lastViewableDiaryDate;
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "group_member_group_id_fkey"))
    private Group group;
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "group_member_member_id_fkey"))
    private Member member;

    public void joinGroup(
            String nickname,
            String profileImage,
            int orderInGroup,
            GroupRole groupRole,
            Group group
    ) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.orderInGroup = orderInGroup;
        this.lastViewableDiaryDate = group.getCreatedAt().toLocalDate().minusDays(1);
        this.groupRole = groupRole;
        this.group = group;
    }

    public void changeGroupRole(GroupRole groupRole) {
        this.groupRole = groupRole;
    }

    public void updateLastViewableDiaryDate() {
        this.lastViewableDiaryDate = LocalDate.now();
    }

    public void updateOrderInGroup(Integer orderInGroup) { this.orderInGroup = orderInGroup; }
}
