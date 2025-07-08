BEGIN;

ALTER TABLE notification DROP CONSTRAINT notification_member_id_key;

COMMIT;
