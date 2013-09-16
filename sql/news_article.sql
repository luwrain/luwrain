create table news_article (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  news_group_id int NOT NULL,
  state int NOT NULL,
  source_url varchar(1024) NOT NULL,
  source_title text NOT NULL,
  uri varchar(1024) NOT NULL,
  title text NOT NULL,
  ext_title text NOT NULL,
  url varchar(1024) NOT NULL,
  descr text NOT NULL,
  author varchar(512) NOT NULL,
  categories varchar(4512) NOT NULL,
  published_date timestamp NOT NULL,
  updated_date timestamp NOT NULL,
  content text NOT NULL
);
