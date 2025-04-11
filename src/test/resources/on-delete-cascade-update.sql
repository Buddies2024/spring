ALTER TABLE "diary" DROP CONSTRAINT IF EXISTS diary_group_member_id_fkey;
ALTER TABLE "diary" ADD CONSTRAINT diary_group_member_id_fkey FOREIGN KEY ("group_member_id") REFERENCES "group_member"("id") ON DELETE CASCADE;

ALTER TABLE "diary_content" DROP CONSTRAINT IF EXISTS diary_content_diary_id_fkey;
ALTER TABLE "diary_content" ADD CONSTRAINT diary_content_diary_id_fkey FOREIGN KEY ("diary_id") REFERENCES "diary"("id") ON DELETE CASCADE;

ALTER TABLE "comment" DROP CONSTRAINT IF EXISTS comment_group_member_id_fkey;
ALTER TABLE "comment" ADD CONSTRAINT comment_group_member_id_fkey FOREIGN KEY ("group_member_id") REFERENCES "group_member"("id") ON DELETE CASCADE;

ALTER TABLE "reply" DROP CONSTRAINT IF EXISTS reply_group_member_id_fkey;
ALTER TABLE "reply" ADD CONSTRAINT reply_group_member_id_fkey FOREIGN KEY ("group_member_id") REFERENCES "group_member"("id") ON DELETE CASCADE;

ALTER TABLE "reply" DROP CONSTRAINT IF EXISTS reply_comment_id_fkey;
ALTER TABLE "reply" ADD CONSTRAINT reply_comment_id_fkey FOREIGN KEY ("comment_id") REFERENCES "comment"("id") ON DELETE CASCADE;