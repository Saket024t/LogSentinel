CREATE TABLE services_metadata (
    id SERIAL PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL UNIQUE,
    team_owner VARCHAR(255) NOT NULL,
    criticality VARCHAR(50) NOT NULL
);

INSERT INTO services_metadata (service_name, team_owner, criticality) VALUES
('order-service', 'commerce-team', 'High'),
('auth-service', 'security-team', 'High');
