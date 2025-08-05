INSERT INTO donor(email, full_name) VALUES ('alice.preprod@example.com','Alice Preprod') ;
INSERT INTO donor(email, full_name) VALUES ('bob.preprod@example.com','Bob Preprod') ;

INSERT INTO payment(reference, amount, method, state) VALUES ('ext-pre-1', 5000, 'card', 'SUCCEEDED');
INSERT INTO donation(donor_id, payment_id, note, created_at) VALUES (1, 1, 'Don preprod 1', now());

INSERT INTO beneficiary(email, full_name) VALUES ('charlie.preprod@example.com','Charlie Preprod');
INSERT INTO payment(reference, amount, method, state) VALUES ('ext-pre-2', 12000, 'bank', 'SUCCEEDED');
INSERT INTO help(beneficiary_id, payment_id, description) VALUES (1, 2, 'Aide preprod 1');
