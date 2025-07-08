CREATE TABLE IF NOT EXISTS reply
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    content VARCHAR(32600) NOT NULL,
    member_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    CONSTRAINT reply_member_id_fkey FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT reply_comment_id_fkey FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE
)
