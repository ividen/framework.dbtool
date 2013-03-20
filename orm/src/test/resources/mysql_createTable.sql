/******   TestEntity(test_entity) ******/
CREATE TABLE test_entity(
	id bigint NOT NULL,
	int_field bigint NULL,
	string_field varchar(255)  NULL,
	date_field TIMESTAMP null,
	short_field bigint null,
	version bigint not NULL,
	entity_aid bigint  null,
	entity_bid bigint  null,
	entity_cid bigint  null,
	PRIMARY KEY (id)
);

/******   TestEntityA(test_entity_a) ******/
CREATE TABLE test_entity_a(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
    PRIMARY KEY (id)
);

/******   TestEntityB(test_entity_b) ******/
CREATE TABLE test_entity_b(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
    PRIMARY KEY (id)
);

/******   TestEntityC(test_entity_c) ******/
CREATE TABLE test_entity_c(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
    entity_eid bigint  null,
    entity_fid bigint  null,
   PRIMARY KEY (id)
);

/******   TestEntityD(test_entity_d) ******/
CREATE TABLE test_entity_d(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
    PRIMARY KEY (id)
);
/******   TestEntityE(test_entity_e) ******/
CREATE TABLE test_entity_e(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
    entity_gid bigint  null,
   PRIMARY KEY (id)
);
/******   TestEntityF(test_entity_f) ******/
CREATE TABLE test_entity_f(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
  PRIMARY KEY (id)
);

/******   TestEntityG(test_entity_g) ******/
CREATE TABLE test_entity_g(
	id bigint NOT NULL,
	title varchar(255)  NULL,
	version bigint not NULL,
  PRIMARY KEY (id)
);