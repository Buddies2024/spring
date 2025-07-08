BEGIN;

-- pk 이름 변경
ALTER TABLE "group" RENAME COLUMN group_id TO id;
ALTER SEQUENCE group_group_id_seq RENAME TO group_id_seq;

ALTER TABLE member RENAME COLUMN member_id TO id;
ALTER SEQUENCE member_member_id_seq RENAME TO member_id_seq;

ALTER TABLE refresh_token RENAME COLUMN refresh_token_id TO id;
ALTER SEQUENCE refresh_token_refresh_token_id_seq RENAME TO refresh_token_id_seq;

ALTER TABLE diary RENAME COLUMN diary_id TO id;
ALTER SEQUENCE diary_diary_id_seq RENAME TO diary_id_seq;

-- fk 이름 변경
ALTER TABLE diary RENAME CONSTRAINT "FK85rgm2b0nreeiqu4aub0rtiu5" TO diary_member_id_fkey;
ALTER TABLE diary RENAME CONSTRAINT "FKnq9k1yrw8u6a7fduy04j12267" TO diary_group_id_fkey;

-- fk cascade on delete 추가
ALTER TABLE diary DROP CONSTRAINT diary_member_id_fkey;
ALTER TABLE diary ADD CONSTRAINT diary_member_id_fkey FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE diary DROP CONSTRAINT diary_group_id_fkey;
ALTER TABLE diary ADD CONSTRAINT diary_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group"(id) ON DELETE CASCADE;

-- uk 이름 변경
-- ALTER TABLE refresh_token RENAME CONSTRAINT "uk_dnbbikqdsc2r2cee1afysqfk9" TO refresh_token_member_id_key;

-- 제약 조건 추가
ALTER TABLE member
ADD CONSTRAINT member_profile_image_check
CHECK (profile_image IN ('red', 'orange', 'yellow', 'green', 'blue', 'navy', 'purple'));

ALTER TABLE member
ADD CONSTRAINT member_order_in_group_check
CHECK (order_in_group >= 0 AND order_in_group <= 7);

ALTER TABLE refresh_token ALTER COLUMN token SET NOT NULL;
ALTER TABLE refresh_token ALTER COLUMN member_id SET NOT NULL;
ALTER TABLE diary ALTER COLUMN member_id SET NOT NULL;
ALTER TABLE diary ALTER COLUMN group_id SET NOT NULL;

-- column 타입 변경
ALTER TABLE "group" ALTER COLUMN "name" type VARCHAR(10);
ALTER TABLE member ALTER COLUMN nickname type VARCHAR(5);
ALTER TABLE member ALTER COLUMN profile_image type VARCHAR(6);
ALTER TABLE member ALTER COLUMN group_role type VARCHAR(12);

COMMIT;
