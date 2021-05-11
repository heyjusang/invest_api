INSERT INTO investor(name, password, role) VALUES ('user1', 'encoded_password', 'ADMIN'); /* id: 1 */
INSERT INTO investor(name, password) VALUES ('user2', 'encoded_password'); /* id: 2 */
INSERT INTO investor(name, password) VALUES ('user3', 'encoded_password'); /* id: 3 */
INSERT INTO investor(name, password) VALUES ('user4', 'encoded_password'); /* id: 4 */
INSERT INTO investor(name, password) VALUES ('user5', 'encoded_password'); /* id: 5 */
INSERT INTO investor(name, password) VALUES ('user6', 'encoded_password'); /* id: 6 */
INSERT INTO investor(name, password) VALUES ('user7', 'encoded_password'); /* id: 7 */
INSERT INTO investor(name, password) VALUES ('user8', 'encoded_password'); /* id: 8 */
INSERT INTO investor(name, password) VALUES ('user9', 'encoded_password'); /* id: 9 */
INSERT INTO investor(name, password) VALUES ('user10', 'encoded_password'); /* id: 10 */
INSERT INTO investor(name, password) VALUES ('user11', 'encoded_password'); /* id: 11 */
INSERT INTO investor(name, password) VALUES ('user12', 'encoded_password'); /* id: 12 */
INSERT INTO investor(name, password) VALUES ('user13', 'encoded_password'); /* id: 13 */

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

INSERT INTO investment(user_id, product_id, amount) VALUES (2, 3, 1000000);
INSERT INTO investment(user_id, product_id, amount) VALUES (4, 3, 1000000);

INSERT INTO product(title, total_investing_amount, current_investing_amount, investor_count, started_at, finished_at) VALUES 
  ('Last chance product', 2000000, 1999999, 2,
    to_date('20210310120000', 'YYYYMMDDHH24MISS'), 
    to_date('20220415120000', 'YYYYMMDDHH24MISS'));

INSERT INTO investment(user_id, product_id, amount) VALUES (2, 4, 1000000);
INSERT INTO investment(user_id, product_id, amount) VALUES (5, 4, 999999);

INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('Not opened product', 2000000,
 to_date('20220210120000', 'YYYYMMDDHH24MISS'),
 to_date('20220215120000', 'YYYYMMDDHH24MISS'));


INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 1', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 2', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 3', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 4', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 5', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 6', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 7', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 8', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 9', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 10', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 11', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 12', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 13', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 14', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 15', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 16', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 17', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 18', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 19', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));
INSERT INTO product(title, total_investing_amount, started_at, finished_at) VALUES
('dummy 20', 2000000, to_date('20210310120020', 'YYYYMMDDHH24MISS'), to_date('20220415120010', 'YYYYMMDDHH24MISS'));

COMMIT;
