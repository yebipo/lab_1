DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'task_status_type') THEN
            CREATE TYPE task_status_type AS ENUM ('TODO', 'IN_PROGRESS', 'DONE');
        END IF;
    END$$;

ALTER TABLE tasks ALTER COLUMN status DROP DEFAULT;
ALTER TABLE tasks ALTER COLUMN status TYPE task_status_type USING status::task_status_type;
ALTER TABLE tasks ALTER COLUMN status SET DEFAULT 'TODO'::task_status_type;