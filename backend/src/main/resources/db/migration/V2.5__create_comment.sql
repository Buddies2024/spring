CREATE TABLE IF NOT EXISTS comment
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    x_coordinate DOUBLE PRECISION NOT NULL,
    y_coordinate DOUBLE PRECISION NOT NULL,
    content VARCHAR(32600) NOT NULL,
    diary_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    CONSTRAINT comment_diary_id_fkey FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE,
    CONSTRAINT comment_member_id_fkey FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
)
