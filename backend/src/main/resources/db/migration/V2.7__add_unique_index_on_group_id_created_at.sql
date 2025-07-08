BEGIN;

CREATE UNIQUE INDEX unique_group_created_at_date ON diary (group_id, DATE(created_at));

COMMIT;
