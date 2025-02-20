package com.exchangediary.group.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
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
    @GeneratedValue(generator = "group_id")
    @GenericGenerator(
            name = "group_id",
            type = com.exchangediary.group.domain.RandomGroupIdGenerator.class
    )
    private String id;
    @NotNull
    private String name;
    @NotNull
    private Integer currentOrder;
    @NotNull
    private LocalDate lastSkipOrderDate;
    @OneToMany(mappedBy = "group")
    @OrderBy("orderInGroup ASC")
    private List<GroupMember> groupMembers;

    public static Group from(String groupName) {
        return Group.builder()
                .name(groupName)
                .currentOrder(1)
                .lastSkipOrderDate(LocalDate.now().minusDays(1))
                .build();
    }

    public void updateCurrentOrder(int currentOrder, int numberOfMembers) {
        if (currentOrder > numberOfMembers) {
            currentOrder = 1;
        }
        this.currentOrder = currentOrder;
    }

    public void updateLastSkipOrderDate() {
        this.lastSkipOrderDate = LocalDate.now();
    }
}
