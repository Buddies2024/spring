CREATE TABLE IF NOT EXISTS notification
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    token VARCHAR(255) NOT NULL,
    member_id BIGINT UNIQUE NOT NULL,
    CONSTRAINT notification_member_id_fkey FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
)
