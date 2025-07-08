BEGIN;

CREATE TABLE IF NOT EXISTS diary_content
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    page INTEGER NOT NULL,
    content VARCHAR(255) NOT NULL,
    diary_id BIGINT NOT NULL,
    CONSTRAINT diary_content_diary_id_fkey FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE
);

INSERT INTO diary_content (created_at, updated_at, page, content, diary_id)
    SELECT created_at, updated_at, 1, SUBSTRING(content, 1, 255), id FROM diary;

INSERT INTO diary_content (created_at, updated_at, page, content, diary_id)
    SELECT created_at, updated_at, 2, SUBSTRING(content, 256, 255), id FROM diary
        WHERE LENGTH(content) >= 256;

INSERT INTO diary_content (created_at, updated_at, page, content, diary_id)
    SELECT created_at, updated_at, 3, SUBSTRING(content, 511, 255), id FROM diary
        WHERE LENGTH(content) >= 511;

ALTER TABLE diary DROP COLUMN IF EXISTS content;

COMMIT;
