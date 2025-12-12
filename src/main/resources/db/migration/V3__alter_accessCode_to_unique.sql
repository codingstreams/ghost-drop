BEGIN;

ALTER TABLE file_metadata
  ALTER COLUMN access_code TYPE VARCHAR(7);

ALTER TABLE file_metadata
  ALTER COLUMN access_code SET NOT NULL;

ALTER TABLE file_metadata
  ADD CONSTRAINT file_metadata_access_code_unique UNIQUE (access_code);

COMMIT;

