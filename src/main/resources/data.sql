INSERT INTO categories (name) VALUES
('Clothing'),
('Toys'),
('Sports'),
('Food'),
('Books'),
('Others');

INSERT INTO promotions
    (category_id, discount, promotion_type, volume_threshold, start_date, end_date, is_active)
VALUES
    (1, 0.35, 'SEASONAL', 1, '2024-01-01', '2025-12-31', true),
    (2, 0.20, 'VOLUME', 5, '2024-01-06', '2025-12-31', true),
    (3, 0.05, 'SEASONAL', 1, '2024-05-12', '2025-12-31', true),
    (4, 0.25, 'VOLUME', 5, '2024-10-01', '2025-12-31', true),
    (5, 0.15, 'SEASONAL', 1, '2024-12-01', '2025-12-31', true),
    (6, 0.00, 'VOLUME', 5, '2024-09-11', '2025-12-31', true);

INSERT INTO products
    (name, description, price, category_Id, weight, current_stock, min_stock)
VALUES
  ('Jacket', 'Something indicate large central measure watch provide.', 100, 1, 3.71, 26, 10),
  ('Building Blocks', 'Agent word occur number chair.', 100, 2, 1.41, 25, 5),
  ('Swimming Goggles', 'Walk range media doctor interest.', 100, 3, 2.51, 113, 15),
  ('Football', 'Country expect price certain different bag everyone.', 21.93, 3, 3.86, 27, 10),
  ('Football', 'Speak value yard here station.', 46.7, 3, 2.99, 82, 20),
  ('Box of Cereal', 'Fear course mean stop practice allow.', 30.46, 4, 3.0, 128, 25),
  ('Fruit Basket', 'Make education get resource challenge.', 6.67, 4, 2.19, 65, 5),
  ('Puzzle', 'Agree go rock over.', 51.44, 2, 3.1, 89, 15),
  ('Hat', 'Police gun first fall ball institution itself recently.', 84.22, 1, 4.53, 37, 8),
  ('Cookbook', 'Own politics door head appear.', 76.88, 5, 2.99, 83, 12),
  ('Picture Frame', 'Public wide thank matter write agreement civil.', 87.82, 6, 3.86, 69, 7),
  ('Doll', 'Weight wonder certain million writer.', 98.8, 2, 3.98, 150, 10),
  ('Puzzle', 'Environmental society what.', 22.85, 2, 4.36, 138, 20),
  ('T-Shirt', 'Want contain raise go list friend attention.', 55.6, 1, 0.91, 21, 15),
  ('Wall Clock', 'Nothing debate doctor should career PM.', 85.85, 6, 3.37, 108, 9),
  ('History Book', 'Direction tough big senior meet after somebody.', 14.82, 5, 0.24, 179, 18),
  ('Swimming Goggles', 'Stay at into.', 4.2, 3, 4.66, 161, 10),
  ('Basketball', 'House authority firm arrive.', 26.11, 3, 3.98, 87, 12),
  ('Puzzle', 'Carry woman cover threat agree his them.', 79.08, 2, 1.73, 56, 10),
  ('Mystery Novel', 'Eat Mr certain federal its.', 75.22, 5, 3.73, 31, 5),
  ('Running Shoes', 'Comfortable and lightweight running shoes.', 75.99, 1, 1.2, 50, 15),
  ('Toy Train Set', 'A complete set of toy train with tracks.', 45.50, 2, 3.5, 40, 10),
  ('Yoga Mat', 'Non-slip, durable yoga mat.', 20.00, 3, 1.8, 100, 20),
  ('Chocolate Box', 'Assorted chocolates in a decorative box.', 15.75, 4, 0.5, 200, 30),
  ('Science Fiction Novel', 'A thrilling science fiction adventure.', 12.99, 5, 0.4, 150, 20),
  ('Photo Album', 'Keep your memories safe in this beautiful photo album.', 18.99, 6, 1.5, 70, 10),
  ('Jeans', 'Comfortable and stylish blue jeans.', 50.00, 1, 0.7, 60, 20),
  ('Stuffed Animal', 'Soft and cuddly teddy bear.', 25.00, 2, 0.4, 80, 15),
  ('Tennis Racket', 'High-quality tennis racket for professional play.', 120.00, 3, 0.6, 30, 5),
  ('Organic Tea', 'A variety pack of organic teas.', 10.50, 4, 0.3, 150, 20),
  ('Biography', 'An inspiring biography of a famous personality.', 22.00, 5, 0.5, 90, 10),
  ('Bluetooth Speaker', 'Portable and powerful Bluetooth speaker.', 35.00, 6, 0.8, 45, 8),
  ('Winter Coat', 'Warm and cozy winter coat.', 120.00, 1, 2.0, 35, 10),
  ('Action Figure', 'Popular action figure from a famous movie.', 15.00, 2, 0.2, 100, 25),
  ('Soccer Ball', 'Standard size soccer ball.', 25.00, 3, 0.5, 60, 10),
  ('Granola Bars', 'Healthy and tasty granola bars.', 12.00, 4, 0.4, 120, 20),
  ('Fantasy Novel', 'An epic tale of adventure and magic.', 18.50, 5, 0.7, 80, 15),
  ('Wall Art', 'Beautiful and modern wall art piece.', 45.00, 6, 1.0, 50, 5),
  ('Sweater', 'Cozy and stylish sweater.', 60.00, 1, 0.8, 40, 10),
  ('Board Game', 'Fun and engaging board game for all ages.', 35.00, 2, 1.2, 70, 10);