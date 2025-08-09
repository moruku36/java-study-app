-- Safe migration to add deleted_at if missing (PostgreSQL)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' AND table_name = 'learning_goals' AND column_name = 'deleted_at'
    ) THEN
        ALTER TABLE public.learning_goals ADD COLUMN deleted_at TIMESTAMP;
    END IF;
END$$;


