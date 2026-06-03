-- Add audit columns to address table
ALTER TABLE address ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE address ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE address ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT FALSE;

-- Add audit columns to contact table
ALTER TABLE contact ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE contact ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE contact ADD COLUMN IF NOT EXISTS is_primary BOOLEAN DEFAULT FALSE;
ALTER TABLE contact ADD COLUMN IF NOT EXISTS mobile VARCHAR(20);

-- Add audit columns to party_role table
ALTER TABLE party_role ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE party_role ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add foreign key constraints for address
ALTER TABLE address DROP CONSTRAINT IF EXISTS fk_address_party;
ALTER TABLE address ADD CONSTRAINT fk_address_party FOREIGN KEY (party_id) REFERENCES party(id);

-- Add foreign key constraints for contact
ALTER TABLE contact DROP CONSTRAINT IF EXISTS fk_contact_party;
ALTER TABLE contact ADD CONSTRAINT fk_contact_party FOREIGN KEY (party_id) REFERENCES party(id);

-- Add foreign key constraints for party_role
ALTER TABLE party_role DROP CONSTRAINT IF EXISTS fk_party_role_party;
ALTER TABLE party_role ADD CONSTRAINT fk_party_role_party FOREIGN KEY (party_id) REFERENCES party(id);
ALTER TABLE party_role DROP CONSTRAINT IF EXISTS fk_party_role_party_type;
ALTER TABLE party_role ADD CONSTRAINT fk_party_role_party_type FOREIGN KEY (party_type_id) REFERENCES party_type(id);