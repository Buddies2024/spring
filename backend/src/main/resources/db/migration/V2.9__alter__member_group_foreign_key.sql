BEGIN;

ALTER TABLE member DROP CONSTRAINT IF EXISTS member_group_id_fkey;
ALTER TABLE member ADD CONSTRAINT member_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group"(id);

COMMIT;
