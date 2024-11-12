package com.exchangediary.comment.domain.entity;

import com.exchangediary.comment.ui.dto.request.CommentCreateRequest;
import com.exchangediary.diary.domain.entity.Diary;
import com.exchangediary.global.domain.entity.BaseEntity;
import com.exchangediary.member.domain.entity.Member;
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
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private final Double xCoordinate;
    @NotNull
    private final Double yCoordinate;
    @Lob
    @JdbcType(LongVarcharJdbcType.class)
    @NotNull
    private final String content;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", foreignKey = @ForeignKey(name = "comment_diary_id_fkey"))
    private final Diary diary;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "comment_member_id_fkey"))
    private final Member member;

    public static Comment of(CommentCreateRequest commentRequest, Member member, Diary diary) {
        return Comment.builder()
                .xCoordinate(commentRequest.xCoordinate())
                .yCoordinate(commentRequest.yCoordinate())
                .content(commentRequest.content())
                .member(member)
                .diary(diary)
                .build();
    }
}
