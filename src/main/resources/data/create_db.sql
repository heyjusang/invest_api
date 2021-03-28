CREATE TABLE investor (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR2(20) UNIQUE NOT NULL,
  password VARCHAR2(200) NOT NULL,
  role VARCHAR2(10) DEFAULT 'USER',
  created_at DATE DEFAULT SYSDATE
);

CREATE TABLE product (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title VARCHAR2(100) NOT NULL,
  total_investing_amount NUMBER NOT NULL,
  current_investing_amount NUMBER DEFAULT 0,
  investor_count NUMBER DEFAULT 0,
  started_at DATE NOT NULL,
  finished_at DATE NOT NULL,
  created_at DATE DEFAULT SYSDATE,
  CONSTRAINT chk_current_investing_amount CHECK (total_investing_amount >= current_investing_amount)
);

CREATE INDEX idx_product ON product(started_at, finished_at);

CREATE TABLE investment (
  id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id NUMBER NOT NULL,
  product_id NUMBER NOT NULL,
  amount NUMBER NOT NULL,
  created_at DATE DEFAULT SYSDATE,
  CONSTRAINT fk_investment FOREIGN KEY(product_id) REFERENCES product(id) ON DELETE CASCADE,
  CONSTRAINT chk_amount CHECK (amount > 0),
  CONSTRAINT uq_user_id_product_id UNIQUE(user_id, product_id)
);

CREATE INDEX idx_investment ON investment(user_id);
