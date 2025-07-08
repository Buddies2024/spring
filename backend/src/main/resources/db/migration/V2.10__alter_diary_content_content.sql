BEGIN;

ALTER TABLE diary_content ALTER COLUMN content TYPE character varying(32600);

COMMIT;
