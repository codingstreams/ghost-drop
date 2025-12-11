BEGIN;

CREATE TABLE file_metadata (
  id VARCHAR(255) PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_type VARCHAR(100) NOT NULL,
  storage_path VARCHAR(255) NOT NULL,
  access_code VARCHAR(100) NOT NULL,
  upload_date TIMESTAMP NOT NULL,
  expiry_date TIMESTAMP NOT NULL,
  max_downloads INT NOT NULL DEFAULT -1
);

COMMIT;