INSERT INTO article (name, reference, category, is_perishable) VALUES ('Banane de l''espace', 'RSTUV', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Côtelettes de porc', 'XYZAB', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Poire', 'POI23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Poulet rôti', 'LMNOP', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Pomme', 'POM23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Filet de saumon', 'YZABCD', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Bacon', 'QRSTUV', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Tomate', 'TOM22', 'VEGETABLE', false);

INSERT INTO article (name, reference, category, is_perishable) VALUES ('Epinard', 'EPI22', 'VEGETABLE', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Chou', 'CHOU22', 'VEGETABLE', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Fraise', 'FRA23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Côtes de veau', 'KLMNOP', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Cerise', 'CER23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Patate', 'PAT22', 'VEGETABLE', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Raisin', 'RAI23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Prune', 'PRU23', 'FRUIT', false);

INSERT INTO article (name, reference, category, is_perishable) VALUES ('Courgette', 'COUR22', 'VEGETABLE', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Framboise', 'FRAM23', 'FRUIT', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Saumon frais', 'IJKDE', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Lettue', 'LET22', 'VEGETABLE', false);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Steak d''agneau', 'EFGHIJ', 'MEAT', true);
INSERT INTO article (name, reference, category, is_perishable) VALUES ('Radis', 'RAD22', 'VEGETABLE', false);

-- INSERT INTO stock (article_reference, quantity) VALUES (1, 2);
-- INSERT INTO stock (article_reference, quantity) VALUES (1, 5);

-- 5 côtelettes
INSERT INTO stock (article_reference, quantity, best_before) VALUES ('XYZAB', 5, '2024-11-01'); -- date ok
-- 5 poulets
INSERT INTO stock (article_reference, quantity, best_before) VALUES ('LMNOP', 5, '2024-11-01'); -- date ok
-- 10 poulets presque périmés
INSERT INTO stock (article_reference, quantity, best_before) VALUES ('LMNOP', 10, '2023-10-17'); -- date trop proche
-- 3 saumons
INSERT INTO stock (article_reference, quantity, best_before) VALUES ('IJKDE', 3, '2023-01-01'); -- date passée
-- 10 bananes
INSERT INTO stock (article_reference, quantity, best_before) VALUES ('RSTUV', 10, '2099-01-01');

-- 85 épinards
INSERT INTO stock (article_reference, quantity) VALUES ('EPI22', 85); -- date ok
-- 10 choux
INSERT INTO stock (article_reference, quantity) VALUES ('CHOU22', 10); -- date trop proche
-- 53 fraises
INSERT INTO stock (article_reference, quantity) VALUES ('FRA23', 53); -- date passée
-- 125 cerises
INSERT INTO stock (article_reference, quantity) VALUES ('CER23', 125);