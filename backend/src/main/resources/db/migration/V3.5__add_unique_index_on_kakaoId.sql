BEGIN;

ALTER TABLE member ADD CONSTRAINT unique_kakaoId UNIQUE (kakao_id);

COMMIT;
