-- Final consolidated insert for home listings with semantic descriptions
INSERT INTO homes (id, price, beds, baths, sqft, image_url, status, street, city, state_code, zip_code, country, description)
VALUES
('859520cb-6f76-4f2a-849e-1f1f97387b9a', 450000.00, 3, 2, 2100, 'assets/home1.jpg', 'AVAILABLE', '123 Maple Ave', 'Springfield', 'IL', '62704', 'USA',
 'Charming and well-maintained 3-bedroom suburban home in the heart of Springfield. Featuring a spacious 2100 sqft layout with plenty of natural light, this classic brick house is perfect for a growing family looking for a quiet neighborhood.'),

('a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 1250000.00, 5, 4, 4500, 'assets/home2.jpg', 'AVAILABLE', '742 Evergreen Terrace', 'Austin', 'TX', '78701', 'USA',
 'Luxurious and modern 5-bedroom estate in the prestigious Austin area. This sprawling 4500 sqft residence boasts high ceilings, a gourmet chef''s kitchen, and a vast backyard oasis, offering the ultimate in upscale Texas living.'),

('b2c3d4e5-f6a7-4b6c-9d0e-1f2a3b4c5d6e', 315000.00, 2, 1, 1100, 'assets/home3.jpg', 'AVAILABLE', '55 Ocean View Dr', 'Miami', 'FL', '33101', 'USA',
 'Cozy coastal bungalow located just steps from the beach in Miami. This 2-bedroom retreat offers a compact but efficient living space, ideal for those seeking a low-maintenance lifestyle with easy access to the ocean and vibrant nightlife.'),

('c3d4e5f6-a7b8-4c7d-0e1f-2a3b4c5d6e7f', 525000.00, 4, 3, 2800, 'assets/home4.jpg', 'AVAILABLE', '890 Birch Lane', 'Seattle', 'WA', '98101', 'USA',
 'Stunning 4-bedroom family home in a lush Seattle neighborhood. Combining modern finishes with Pacific Northwest charm, this 2800 sqft house features energy-efficient systems and a beautiful deck perfect for entertaining year-round.'),

('d4e5f6a7-b8c9-4d8e-1f2a-3b4c5d6e7f8a', 275000.00, 2, 2, 1400, 'assets/home5.jpg', 'PENDING', '12 Oak Creek Way', 'Boulder', 'CO', '80301', 'USA',
 'Charming townhome in Boulder with breathtaking mountain views. This 2-bedroom property features an open-concept living area and easy access to hiking trails, making it perfect for outdoor enthusiasts and active professionals.'),

('e5f6a7b8-c9d0-4e9f-2a3b-4c5d6e7f8a9b', 890000.00, 3, 2, 2200, 'assets/home6.jpg', 'AVAILABLE', '404 Innovation Blvd', 'San Francisco', 'CA', '94105', 'USA',
 'Sleek, tech-ready 3-bedroom condo in the heart of San Francisco''s Innovation District. A true urban sanctuary with 2200 sqft of living space, floor-to-ceiling windows, and smart home integration, just minutes from the city''s top tech hubs.'),

('f6a7b8c9-d0e1-4f0a-3b4c-5d6e7f8a9b0c', 610000.00, 4, 4, 3100, 'assets/home7.jpg', 'AVAILABLE', '202 Heritage Ct', 'Atlanta', 'GA', '30301', 'USA',
 'Elegant 4-bedroom traditional home in a quiet Atlanta cul-de-sac. This 3100 sqft property blends southern hospitality with modern convenience, featuring a grand foyer, formal dining room, and a beautifully landscaped private garden.'),

('0a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', 720000.00, 3, 3, 2600, 'assets/home8.jpg', 'AVAILABLE', '77 Summit Peak', 'Denver', 'CO', '80201', 'USA',
 'Contemporary 3-bedroom mountain retreat in Denver. Offering a perfect balance of luxury and comfort, this 2600 sqft home features high-end finishes, a cozy fireplace, and stunning views of the Front Range from every room.'),

('1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 425000.00, 3, 2, 1950, 'assets/home9.jpg', 'AVAILABLE', '303 Old Town Sq', 'Charleston', 'SC', '29401', 'USA',
 'Historic-style 3-bedroom home in the charming Old Town area of Charleston. This 1950 sqft residence captures the essence of coastal South Carolina with its large front porch, hardwood floors, and proximity to local boutiques and dining.')
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description;