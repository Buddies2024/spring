ALTER TABLE "group" ADD COLUMN member_count INTEGER NOT NULL DEFAULT 0 CHECK (member_count <= 7);
UPDATE "group" g SET member_count = (SELECT count(*) FROM group_member gm WHERE gm.group_id = g.id);
