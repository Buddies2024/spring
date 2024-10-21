package com.exchangediary.member.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.group.domain.entity.Group;
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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "member_group_id_fkey"))
    private Group group;

    public void updateMemberGroupInfo(
            String nickname,
            String profileImage,
            int orderInGroup,
            GroupRole groupRole,
            Group group
    ) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.orderInGroup = orderInGroup;
        this.groupRole = groupRole;
        this.group = group;
    }

    public void changeGroupRole(GroupRole groupRole) {
        this.groupRole = groupRole;
    }
}
