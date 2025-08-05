INSERT INTO donor(email, full_name) VALUES ('alice.prod@example.com','Alice Prod') ;
INSERT INTO payment(reference, amount, method, state) VALUES ('ext-prod-1', 10000, 'card', 'SUCCEEDED');
INSERT INTO donation(donor_id, payment_id, note, created_at) VALUES (1, 1, 'Don prod 1', now());

INSERT INTO beneficiary(email, full_name) VALUES ('zara.prod@example.com','Zara Prod');
INSERT INTO payment(reference, amount, method, state) VALUES ('ext-prod-2', 20000, 'mobile', 'SUCCEEDED');
INSERT INTO help(beneficiary_id, payment_id, description) VALUES (1, 2, 'Aide prod 1');
