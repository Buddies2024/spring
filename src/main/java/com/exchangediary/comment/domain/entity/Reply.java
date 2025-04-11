package com.exchangediary.comment.domain.entity;

import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.group.domain.entity.GroupMember;
import com.exchangediary.comment.ui.dto.request.ReplyCreateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED, force = true)
@AllArgsConstructor(access = PRIVATE)
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @JdbcType(LongVarcharJdbcType.class)
    @NotNull
    private final String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", foreignKey = @ForeignKey(name = "reply_group_member_id_fkey"))
    @NotNull
    private final GroupMember groupMember;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "reply_comment_id_fkey"))
    @NotNull
    private final Comment comment;

    public static Reply of(String content, GroupMember groupMember, Comment comment) {
        return Reply.builder()
                .content(content)
                .groupMember(groupMember)
                .comment(comment)
                .build();
    }
}
