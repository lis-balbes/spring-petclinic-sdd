INSERT IGNORE INTO vets VALUES (1, 'James', 'Carter');
INSERT IGNORE INTO vets VALUES (2, 'Helen', 'Leary');
INSERT IGNORE INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT IGNORE INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT IGNORE INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT IGNORE INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT IGNORE INTO specialties VALUES (1, 'radiology');
INSERT IGNORE INTO specialties VALUES (2, 'surgery');
INSERT IGNORE INTO specialties VALUES (3, 'dentistry');

INSERT IGNORE INTO vet_specialties VALUES (2, 1);
INSERT IGNORE INTO vet_specialties VALUES (3, 2);
INSERT IGNORE INTO vet_specialties VALUES (3, 3);
INSERT IGNORE INTO vet_specialties VALUES (4, 2);
INSERT IGNORE INTO vet_specialties VALUES (5, 1);

-- James Carter (vet 1): Mon-Fri 09:00-17:00
INSERT IGNORE INTO vet_schedules VALUES (1, 1, 1, '09:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (2, 1, 2, '09:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (3, 1, 3, '09:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (4, 1, 4, '09:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (5, 1, 5, '09:00', '17:00');
-- Helen Leary (vet 2): Mon, Wed, Fri 08:00-16:00
INSERT IGNORE INTO vet_schedules VALUES (6, 2, 1, '08:00', '16:00');
INSERT IGNORE INTO vet_schedules VALUES (7, 2, 3, '08:00', '16:00');
INSERT IGNORE INTO vet_schedules VALUES (8, 2, 5, '08:00', '16:00');
-- Linda Douglas (vet 3): Tue-Sat 09:00-18:00
INSERT IGNORE INTO vet_schedules VALUES (9, 3, 2, '09:00', '18:00');
INSERT IGNORE INTO vet_schedules VALUES (10, 3, 3, '09:00', '18:00');
INSERT IGNORE INTO vet_schedules VALUES (11, 3, 4, '09:00', '18:00');
INSERT IGNORE INTO vet_schedules VALUES (12, 3, 5, '09:00', '18:00');
INSERT IGNORE INTO vet_schedules VALUES (13, 3, 6, '09:00', '18:00');
-- Rafael Ortega (vet 4): Mon-Thu 10:00-17:00
INSERT IGNORE INTO vet_schedules VALUES (14, 4, 1, '10:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (15, 4, 2, '10:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (16, 4, 3, '10:00', '17:00');
INSERT IGNORE INTO vet_schedules VALUES (17, 4, 4, '10:00', '17:00');
-- Henry Stevens (vet 5): Mon-Fri 09:00-15:00
INSERT IGNORE INTO vet_schedules VALUES (18, 5, 1, '09:00', '15:00');
INSERT IGNORE INTO vet_schedules VALUES (19, 5, 2, '09:00', '15:00');
INSERT IGNORE INTO vet_schedules VALUES (20, 5, 3, '09:00', '15:00');
INSERT IGNORE INTO vet_schedules VALUES (21, 5, 4, '09:00', '15:00');
INSERT IGNORE INTO vet_schedules VALUES (22, 5, 5, '09:00', '15:00');
-- Sharon Jenkins (vet 6): no schedule

INSERT IGNORE INTO types VALUES (1, 'cat');
INSERT IGNORE INTO types VALUES (2, 'dog');
INSERT IGNORE INTO types VALUES (3, 'lizard');
INSERT IGNORE INTO types VALUES (4, 'snake');
INSERT IGNORE INTO types VALUES (5, 'bird');
INSERT IGNORE INTO types VALUES (6, 'hamster');

INSERT IGNORE INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT IGNORE INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT IGNORE INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT IGNORE INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT IGNORE INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT IGNORE INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT IGNORE INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT IGNORE INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT IGNORE INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT IGNORE INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT IGNORE INTO pets VALUES (1, 'Leo', '2000-09-07', 1, 1);
INSERT IGNORE INTO pets VALUES (2, 'Basil', '2002-08-06', 6, 2);
INSERT IGNORE INTO pets VALUES (3, 'Rosy', '2001-04-17', 2, 3);
INSERT IGNORE INTO pets VALUES (4, 'Jewel', '2000-03-07', 2, 3);
INSERT IGNORE INTO pets VALUES (5, 'Iggy', '2000-11-30', 3, 4);
INSERT IGNORE INTO pets VALUES (6, 'George', '2000-01-20', 4, 5);
INSERT IGNORE INTO pets VALUES (7, 'Samantha', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (8, 'Max', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (9, 'Lucky', '1999-08-06', 5, 7);
INSERT IGNORE INTO pets VALUES (10, 'Mulligan', '1997-02-24', 2, 8);
INSERT IGNORE INTO pets VALUES (11, 'Freddy', '2000-03-09', 5, 9);
INSERT IGNORE INTO pets VALUES (12, 'Lucky', '2000-06-24', 2, 10);
INSERT IGNORE INTO pets VALUES (13, 'Sly', '2002-06-08', 1, 10);

INSERT IGNORE INTO visits VALUES (1, 7, 1, '2010-03-04', '09:00', 'rabies shot');
INSERT IGNORE INTO visits VALUES (2, 8, 2, '2011-03-04', '10:00', 'rabies shot');
INSERT IGNORE INTO visits VALUES (3, 8, 3, '2009-06-04', '11:00', 'neutered');
INSERT IGNORE INTO visits VALUES (4, 7, 4, '2008-09-04', '14:00', 'spayed');
