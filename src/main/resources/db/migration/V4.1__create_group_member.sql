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
    CONSTRAINT group_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT group_member_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group" (id) ON DELETE CASCADE,
    CONSTRAINT group_member_order_in_group_check CHECK (order_in_group BETWEEN 1 AND 7),
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

ALTER TABLE diary ADD COLUMN group_member_id BIGINT;
ALTER TABLE diary ADD CONSTRAINT diary_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id) ON DELETE CASCADE;
UPDATE diary d SET group_member_id = (SELECT gm.id FROM group_member gm WHERE d.member_id = gm.member_id);
ALTER TABLE diary DROP COLUMN member_id;
ALTER TABLE diary ALTER COLUMN group_member_id SET NOT NULL;

ALTER TABLE comment ADD COLUMN group_member_id BIGINT;
ALTER TABLE comment ADD CONSTRAINT comment_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id) ON DELETE CASCADE;
UPDATE comment c SET group_member_id = (SELECT gm.id FROM group_member gm WHERE c.member_id = gm.member_id);
ALTER TABLE comment DROP COLUMN member_id;
ALTER TABLE comment ALTER COLUMN group_member_id SET NOT NULL;

ALTER TABLE reply ADD COLUMN group_member_id BIGINT;
ALTER TABLE reply ADD CONSTRAINT reply_group_member_id_fkey FOREIGN KEY (group_member_id) REFERENCES group_member(id) ON DELETE CASCADE;
UPDATE reply r SET group_member_id = (SELECT gm.id FROM group_member gm WHERE r.member_id = gm.member_id);
ALTER TABLE reply DROP COLUMN member_id;
ALTER TABLE reply ALTER COLUMN group_member_id SET NOT NULL;
