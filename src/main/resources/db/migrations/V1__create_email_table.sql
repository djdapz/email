CREATE TABLE sent_emails
(
    request    JSON      NOT NULL,
    date       TIMESTAMP NOT NULL DEFAULT NOW(),
    is_success BOOLEAN   NOT NULL
)