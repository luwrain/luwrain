CREATE TABLE registry_dir (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(512) NOT NULL,
  parent_id INT NOT NULL
);

CREATE TABLE registry_value (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(512) NOT NULL,
  dir_id INT NOT NULL,
  value_type INT NOT NULL,
  int_value INT NOT NULL,
  str_value text NOT NULL,
  bool_value BOOLEAN NOT NULL
);

CREATE TABLE news_article (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  news_group_id int NOT NULL,
  state int NOT NULL,
  source_url varchar(2048) NOT NULL,
  source_title text NOT NULL,
  uri varchar(2048) NOT NULL,
  title text NOT NULL,
  ext_title text NOT NULL,
  url varchar(2048) NOT NULL,
  descr text NOT NULL,
  author text NOT NULL,
  categories varchar(1024) NOT NULL,
  published_date timestamp NOT NULL,
  updated_date timestamp NOT NULL,
  content text NOT NULL
);

CREATE TABLE mail_message (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mail_group_id  int NOT NULL,
  state int NOT NULL,
  from_addr varchar(512) NOT NULL,
  to_addr varchar(512) NOT NULL,
  subject varchar(1024) NOT NULL,
  msg_date timestamp NOT NULL,
  raw_msg text NOT NULL,
  content text NOT NULL,
  ext_info text NOT NULL
);

CREATE TABLE mail_message_to_address (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mail_message_id  int NOT NULL,
  value varchar(512) NOT NULL
);

CREATE TABLE mail_message_attachment (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mail_message_id  int NOT NULL,
  value varchar(2048) NOT NULL
);


INSERT INTO registry_dir (id,name,parent_id) VALUES (1,'root',1);
INSERT INTO registry_dir (id,name,parent_id) VALUES (2,'org',1);
INSERT INTO registry_dir (id,name,parent_id) VALUES (3,'luwrain',2);

-- The workaround as in *.reg files we cannot use string with spaces only --
INSERT INTO registry_dir (id,name,parent_id) VALUES (4,'global-keys',3);
INSERT INTO registry_dir (id,name,parent_id) VALUES (5,'cut-copy-point',4);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',5,2,' ');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-control',5,3,true);

