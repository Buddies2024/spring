package com.exchangediary.group.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.member.domain.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Integer currentOrder;
    @NotNull
    private String code;
    @OneToMany(mappedBy = "group")
    private List<Member> members;

    public static Group of(String groupName, String code) {
        return Group.builder()
                .name(groupName)
                .currentOrder(1)
                .code(code)
                .build();
    }

    public void updateCurrentOrder(Integer currentOrder) {
        this.currentOrder = currentOrder;
    }
}
