CREATE TABLE IF NOT EXISTS group_member
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    nickname VARCHAR(5) NOT NULL,
    profile_image VARCHAR(255) NOT NULL,
    order_in_group INTEGER NOT NULL,
    last_viewable_diary_date DATE NOT NULL,
    group_role VARCHAR(255) CHECK (group_role IN ('GROUP_LEADER', 'GROUP_MEMBER')) NOT NULL,
    member_id BIGINT NOT NULL,
    group_id VARCHAR(8) NOT NULL,
    CONSTRAINT group_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES "member" (id),
    CONSTRAINT group_member_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group" (id),
    CONSTRAINT group_member_order_in_group_check CHECK (order_in_group >= 0 AND order_in_group <= 7),
    CONSTRAINT group_member_profile_image_check CHECK (profile_image IN ('red', 'orange', 'yellow', 'green', 'blue', 'navy', 'purple'))
);

INSERT INTO group_member (created_at, updated_at, nickname, profile_image, order_in_group, last_viewable_diary_date, group_role, member_id, group_id)
    SELECT created_at, updated_at, nickname, profile_image, order_in_group, last_viewable_diary_date, group_role, id, group_id FROM member WHERE group_id IS NOT NULL;
ALTER TABLE member
    DROP COLUMN nickname,
    DROP COLUMN profile_image,
    DROP COLUMN order_in_group,
    DROP COLUMN last_viewable_diary_date,
    DROP COLUMN group_role,
    DROP COLUMN group_id;

ALTER TABLE diary ADD COLUMN group_member_id BIGINT NOT NULL;
ALTER TABLE diary ADD CONSTRAINT diary_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id);
UPDATE diary SET group_member_id = member_id;
ALTER TABLE diary DROP COLUMN member_id;

ALTER TABLE comment ADD COLUMN group_member_id BIGINT NOT NULL;
ALTER TABLE comment ADD CONSTRAINT comment_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id);
UPDATE comment SET group_member_id = member_id;
ALTER TABLE comment DROP COLUMN member_id;

ALTER TABLE reply ADD COLUMN group_member_id BIGINT NOT NULL;
ALTER TABLE reply ADD CONSTRAINT reply_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id);
UPDATE reply SET group_member_id = member_id;
ALTER TABLE reply DROP COLUMN member_id;
