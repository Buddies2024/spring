BEGIN;

-- diary 테이블에 image_file_name 컬럼 추가
ALTER TABLE diary ADD COLUMN IF NOT EXISTS image_file_name VARCHAR(15);
ALTER TABLE diary ALTER COLUMN image_file_name SET DATA TYPE VARCHAR(15);

UPDATE diary
     SET image_file_name = to_char(diary.created_at, 'YYYYMMdd."jpg"')
        FROM upload_image ui
        WHERE ui.diary_id = diary.id;

-- upload image 제약조건 제거
ALTER TABLE upload_image DROP CONSTRAINT IF EXISTS upload_image_diary_id_fkey;
ALTER TABLE upload_image DROP CONSTRAINT IF EXISTS upload_image_diary_id_key;

COMMIT;
