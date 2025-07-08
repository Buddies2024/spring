BEGIN;

-- group pk 타입 변경
ALTER TABLE member DROP CONSTRAINT IF EXISTS member_group_id_fkey;
ALTER TABLE diary DROP CONSTRAINT IF EXISTS diary_group_id_fkey;

ALTER TABLE "group" ALTER COLUMN id SET DATA TYPE VARCHAR(8);
ALTER TABLE member ALTER COLUMN group_id SET DATA TYPE VARCHAR(8);
ALTER TABLE diary ALTER COLUMN group_id SET DATA TYPE VARCHAR(8);

ALTER TABLE member ADD CONSTRAINT member_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group"(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE diary ADD CONSTRAINT diary_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group"(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- group code 열 제거
ALTER TABLE "group" DROP COLUMN code;

-- group id 값
UPDATE "group" SET id = substr(CAST(gen_random_uuid() AS TEXT), 1, 8);

COMMIT;
