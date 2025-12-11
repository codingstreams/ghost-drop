BEGIN;

ALTER TABLE file_metadata
  ALTER COLUMN id TYPE uuid
  USING id::uuid;

ALTER TABLE file_metadata
  ALTER COLUMN id SET NOT NULL;

COMMIT;