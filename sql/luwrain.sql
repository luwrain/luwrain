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

create table news_article (
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

-- Basic registry directories --

INSERT INTO registry_dir (id,name,parent_id) VALUES (1,'root',1);
INSERT INTO registry_dir (id,name,parent_id) VALUES (2,'org',1);
INSERT INTO registry_dir (id,name,parent_id) VALUES (3,'luwrain',2);
INSERT INTO registry_dir (id,name,parent_id) VALUES (4,'main-menu',3);
INSERT INTO registry_dir (id,name,parent_id) VALUES (5,'pim',3);
INSERT INTO registry_dir (id,name,parent_id) VALUES (6,'sounds',3);

INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('content',4,2,'mail:news:message:fetch::commander:notepad:calendar::control:registry::quit');

-- PIM --

INSERT INTO registry_dir (id,name,parent_id) VALUES (7,'mail',5);
INSERT INTO registry_dir (id,name,parent_id) VALUES (8,'news',5);

-- Mail --

INSERT INTO registry_dir (id,name,parent_id) VALUES (9,'accounts',7);
INSERT INTO registry_dir (id,name,parent_id) VALUES (10,'groups',7);
INSERT INTO registry_dir (id,name,parent_id) VALUES (11,'storing',7);

-- Mail storing --

INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('type',11,2,'jdbc');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('url',11,2,'jdbc:mysql://localhost/luwrain?characterEncoding=utf-8');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('driver',11,2,'com.mysql.jdbc.Driver');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('login',11,2,'root');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('passwd',11,2,'');

-- Default mail account --

INSERT INTO registry_dir (id,name,parent_id) VALUES (12,'1',9);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',12,2,'gmail.com');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('type',12,2,'incoming');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('protocol',12,2,'pop3-ssl');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('host',12,2,'pop.gmail.com');
INSERT INTO registry_value (name,dir_id,value_type,int_value) VALUES ('port',12,1,995);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('login',12,2,'');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('hpasswd',12,2,'');

-- Mail groups --

INSERT INTO registry_dir (id,name,parent_id) VALUES (13,'1',10);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',13,2,'Mail groups');
INSERT INTO registry_value (name,dir_id,value_type,int_value) VALUES ('parent',13,2,1);

INSERT INTO registry_dir (id,name,parent_id) VALUES (14,'2',10);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',14,2,'Inbox');
INSERT INTO registry_value (name,dir_id,value_type,int_value) VALUES ('parent',14,2,1);

INSERT INTO registry_dir (id,name,parent_id) VALUES (15,'3',10);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',15,2,'Sent');
INSERT INTO registry_value (name,dir_id,value_type,int_value) VALUES ('parent',15,2,1);

INSERT INTO registry_dir (id,name,parent_id) VALUES (16,'4',10);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',16,2,'Drafts');
INSERT INTO registry_value (name,dir_id,value_type,int_value) VALUES ('parent',16,2,1);

-- News --

INSERT INTO registry_dir (id,name,parent_id) VALUES (20,'groups',8);
INSERT INTO registry_dir (id,name,parent_id) VALUES (21,'storing',8);

-- News storing --

INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('type',21,2,'jdbc');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('url',21,2,'jdbc:mysql://localhost/luwrain?characterEncoding=utf-8');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('driver',21,2,'com.mysql.jdbc.Driver');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('login',21,2,'root');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('passwd',21,2,'');

-- News groups --

INSERT INTO registry_dir (id,name,parent_id) VALUES (22,'1',20);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',22,2,'TechCrunch');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('url1',22,2,'http://feeds.feedburner.com/TechCrunch/');

INSERT INTO registry_dir (id,name,parent_id) VALUES (23,'2',20);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',23,2,'Reuters');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('url1',23,2,'http://feeds.reuters.com/reuters/MostRead');

INSERT INTO registry_dir (id,name,parent_id) VALUES (24,'3',20);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('name',24,2,'Tech in Asia');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('url1',24,2,'http://feeds2.feedburner.com/PennOlson');

-- Sounds --

INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('event-not-processed',6,2,'sounds/piano/beep1.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('no-applications',6,2,'sounds/piano/no-applications.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('startup',6,2,'sounds/piano/startup.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('shutdown',6,2,'sounds/piano/beep1.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('main-menu',6,2,'sounds/piano/main-menu.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('main-menu-item',6,2,'sounds/piano/main-menu-item.wav');
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('main-menu-empty-line',6,2,'sounds/piano/main-menu-empty-line.wav');

-- Global keys --

INSERT INTO registry_dir (id,name,parent_id) VALUES (30,'global-keys',3);
INSERT INTO registry_dir (id,name,parent_id) VALUES (31,'save',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',31,2,'f2');
INSERT INTO registry_dir (id,name,parent_id) VALUES (32,'open',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',32,2,'f3');
INSERT INTO registry_dir (id,name,parent_id) VALUES (33,'close',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',33,2,'f4');

-- App and area switching --

INSERT INTO registry_dir (id,name,parent_id) VALUES (34,'switch-next-app',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',34,2,'tab');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-left-alt',34,3,true);
INSERT INTO registry_dir (id,name,parent_id) VALUES (35,'switch-next-area',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',35,2,'tab');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-control',35,3,true);

-- Copy-paste --

INSERT INTO registry_dir (id,name,parent_id) VALUES (36,'copy-cut-point',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',36,2,' ');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-control',36,3,true);
INSERT INTO registry_dir (id,name,parent_id) VALUES (37,'copy',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',37,2,'insert');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-control',37,3,true);
INSERT INTO registry_dir (id,name,parent_id) VALUES (38,'paste',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',38,2,'insert');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-shift',38,3,true);

-- Main menu and quit --

INSERT INTO registry_dir (id,name,parent_id) VALUES (39,'main-menu',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('non-character',39,2,'windows');
INSERT INTO registry_dir (id,name,parent_id) VALUES (40,'quit',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',40,2,'q');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-left-alt',40,3,true);

-- OK (Ctrl-c) --

INSERT INTO registry_dir (id,name,parent_id) VALUES (41,'ok',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',41,2,'c');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-control',41,3,true);

-- Screen font size --

INSERT INTO registry_dir (id,name,parent_id) VALUES (50,'increase-font-size',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',50,2,'=');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-left-alt',50,3,true);
INSERT INTO registry_dir (id,name,parent_id) VALUES (51,'decrease-font-size',30);
INSERT INTO registry_value (name,dir_id,value_type,str_value) VALUES ('character',51,2,'-');
INSERT INTO registry_value (name,dir_id,value_type,bool_value) VALUES ('with-left-alt',51,3,true);
