BEGIN;

ALTER TABLE notification ADD CONSTRAINT unique_token UNIQUE (token);

COMMIT;
