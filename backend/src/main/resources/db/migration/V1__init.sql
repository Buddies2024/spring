CREATE TABLE "group"
(
    group_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    current_order INTEGER NOT NULL,
    last_skip_order_date DATE NOT NULL
);

CREATE TABLE member
(
    member_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    kakao_id BIGINT NOT NULL,
    nickname VARCHAR(255),
    profile_image VARCHAR(255),
    order_in_group INTEGER,
    last_viewable_diary_date DATE,
    group_role VARCHAR(255) CHECK (group_role IN ('GROUP_LEADER', 'GROUP_MEMBER')),
    group_id BIGINT,
    CONSTRAINT member_group_id_fkey FOREIGN KEY (group_id) REFERENCES "group" (group_id)
);

CREATE TABLE refresh_token
(
    refresh_token_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    token VARCHAR(255),
    member_id BIGINT UNIQUE,
    CONSTRAINT "uk_dnbbikqdsc2r2cee1afysqfk9" UNIQUE (member_id),
    CONSTRAINT refresh_token_member_id_fkey FOREIGN KEY (member_id) REFERENCES member (member_id) ON DELETE CASCADE
);

CREATE TABLE diary
(
    diary_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    content VARCHAR(32600) NOT NULL,
    mood_location VARCHAR(255) NOT NULL,
    member_id BIGINT,
    group_id BIGINT,
    CONSTRAINT "FK85rgm2b0nreeiqu4aub0rtiu5" FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT "FKnq9k1yrw8u6a7fduy04j12267" FOREIGN KEY (group_id) REFERENCES "group" (group_id)
);

CREATE TABLE upload_image
(
    upload_image_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    image BYTEA NOT NULL,
    diary_id BIGINT UNIQUE,
    CONSTRAINT upload_image_diary_id_key UNIQUE (diary_id),
    CONSTRAINT upload_image_diary_id_fkey FOREIGN KEY (diary_id) REFERENCES diary (diary_id) ON DELETE CASCADE
);
