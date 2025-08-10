-- Add missing flags to learning_goals if they don't exist (PostgreSQL)
DO $$
BEGIN
    -- Add is_deleted
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' AND table_name = 'learning_goals' AND column_name = 'is_deleted'
    ) THEN
        ALTER TABLE public.learning_goals ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
        UPDATE public.learning_goals SET is_deleted = FALSE WHERE is_deleted IS NULL;
    END IF;

    -- Add is_active
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' AND table_name = 'learning_goals' AND column_name = 'is_active'
    ) THEN
        ALTER TABLE public.learning_goals ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
        UPDATE public.learning_goals SET is_active = TRUE WHERE is_active IS NULL;
    END IF;
END$$;


