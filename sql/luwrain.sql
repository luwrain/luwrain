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
