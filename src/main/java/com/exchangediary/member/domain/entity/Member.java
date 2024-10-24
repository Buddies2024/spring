package com.exchangediary.member.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.group.domain.entity.Group;
import jakarta.persistence.CascadeType;
import com.exchangediary.member.domain.enums.GroupRole;
import jakarta.persistence.Column;
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
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @NotNull
    private final Long kakaoId;
    private String nickname;
    private String profileImage;
    private Integer orderInGroup;
    private LocalDate lastViewableDiaryDate;
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "member_group_id_fkey"))
    private Group group;
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    public static Member from(Long kakaoId) {
        return Member.builder()
                .kakaoId(kakaoId)
                .orderInGroup(0)
                .build();
    }

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
        this.lastViewableDiaryDate = LocalDate.now();
        this.groupRole = groupRole;
        this.group = group;
    }

    public void changeGroupRole(GroupRole groupRole) {
        this.groupRole = groupRole;
    }

    public void updateLastViewableDiaryDate(LocalDate lastViewableDiaryDate) {
        this.lastViewableDiaryDate = lastViewableDiaryDate;
    }

    public void issueRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateOrderInGroup(Integer orderInGroup) { this.orderInGroup = orderInGroup; }
}
