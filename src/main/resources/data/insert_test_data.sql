INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
  ('Normal product', 2000000, 
    to_date('20210310120000', 'YYYYMMDDHH24MISS'), 
    to_date('20220415120000', 'YYYYMMDDHH24MISS'));

INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES 
  ('Closed product', 2000000,
    to_date('20210210120000', 'YYYYMMDDHH24MISS'), 
    to_date('20210215120000', 'YYYYMMDDHH24MISS'));

INSERT INTO product(title, total_investing_amount, current_investing_amount, investor_count, started_at, finished_at) VALUES 
  ('Sold out product', 2000000, 2000000, 2, 
    to_date('20210310120000', 'YYYYMMDDHH24MISS'), 
    to_date('20220415120000', 'YYYYMMDDHH24MISS'));

INSERT INTO investment(user_id, product_id, amount) VALUES (10, 3, 1000000);
INSERT INTO investment(user_id, product_id, amount) VALUES (11, 3, 1000000);

INSERT INTO product(title, total_investing_amount, current_investing_amount, investor_count, started_at, finished_at) VALUES 
  ('Last chance product', 2000000, 1999999, 2,
    to_date('20210310120000', 'YYYYMMDDHH24MISS'), 
    to_date('20220415120000', 'YYYYMMDDHH24MISS'));

INSERT INTO investment(user_id, product_id, amount) VALUES (10, 4, 1000000);
INSERT INTO investment(user_id, product_id, amount) VALUES (12, 4, 999999);

INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('Not opened product', 2000000,
 to_date('20220210120000', 'YYYYMMDDHH24MISS'),
 to_date('20220215120000', 'YYYYMMDDHH24MISS'));

INSERT INTO product(title, total_investing_amount, started_at, finished_at)
  SELECT 'dummy product ' || level, 2000000, 
    to_date('202103101200' || level + 20, 'YYYYMMDDHH24MISS'), 
    to_date('202204151200' || level + 10, 'YYYYMMDDHH24MISS') FROM dual CONNECT BY LEVEL <= 20;

COMMIT;
