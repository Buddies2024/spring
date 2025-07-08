BEGIN;

ALTER TABLE diary RENAME COLUMN mood_location TO today_mood;

UPDATE diary n SET today_mood = (SELECT substring(o.today_mood, 32) FROM diary o WHERE n.id = o.id);

COMMIT;
