-- Semantic-friendly descriptions (Max 120 chars) for natural language search
INSERT INTO homes (id, price, beds, baths, sqft, image_url, status, street, city, state_code, zip_code, country, description)
VALUES
('859520cb-6f76-4f2a-849e-1f1f97387b9a', 450000.00, 3, 2, 2100, 'assets/home1.jpg', 'AVAILABLE', '123 Maple Ave', 'Springfield', 'IL', '62704', 'USA',
 'Traditional brick house with sun-drenched rooms and a large fenced backyard.'),

('a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 1250000.00, 5, 4, 4500, 'assets/home2.jpg', 'AVAILABLE', '742 Evergreen Terrace', 'Austin', 'TX', '78701', 'USA',
 'Expansive contemporary estate with floor-to-ceiling glass and a resort-style pool.'),

('b2c3d4e5-f6a7-4b6c-9d0e-1f2a3b4c5d6e', 315000.00, 2, 1, 1100, 'assets/home3.jpg', 'AVAILABLE', '55 Ocean View Dr', 'Miami', 'FL', '33101', 'USA',
 'Light-filled seaside cottage with airy interiors and a sunset deck over the water.'),

('c3d4e5f6-a7b8-4c7d-0e1f-2a3b4c5d6e7f', 525000.00, 4, 3, 2800, 'assets/home4.jpg', 'AVAILABLE', '890 Birch Lane', 'Seattle', 'WA', '98101', 'USA',
 'Sustainable craftsman home in the trees with natural wood and energy-efficient design.'),

('d4e5f6a7-b8c9-4d8e-1f2a-3b4c5d6e7f8a', 275000.00, 2, 2, 1400, 'assets/home5.jpg', 'PENDING', '12 Oak Creek Way', 'Boulder', 'CO', '80301', 'USA',
 'Rustic mountain-side townhome with stone accents and direct access to hiking trails.'),

('e5f6a7b8-c9d0-4e9f-2a3b-4c5d6e7f8a9b', 890000.00, 3, 2, 2200, 'assets/home6.jpg', 'AVAILABLE', '404 Innovation Blvd', 'San Francisco', 'CA', '94105', 'USA',
 'Polished industrial loft in city center with exposed brick and smart technology.'),

('f6a7b8c9-d0e1-4f0a-3b4c-5d6e7f8a9b0c', 610000.00, 4, 4, 3100, 'assets/home7.jpg', 'AVAILABLE', '202 Heritage Ct', 'Atlanta', 'GA', '30301', 'USA',
 'Stately colonial residence on a quiet cul-de-sac with manicured formal gardens.'),

('0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', 720000.00, 3, 3, 2600, 'assets/home8.jpg', 'AVAILABLE', '77 Summit Peak', 'Denver', 'CO', '80201', 'USA',
 'Minimalist mountain retreat with clean architectural lines and panoramic snowy views.'),

('1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 425000.00, 3, 2, 1950, 'assets/home9.jpg', 'AVAILABLE', '303 Old Town Sq', 'Charleston', 'SC', '29401', 'USA',
 'Historic southern jewel with a wrap-around porch and original pine floors.')
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description;