/******   TestEntity(test_entity) ******/
CREATE TABLE test_entity
	id NUMBER NOT NULL,
	int_field NUMBER NULL,
	string_field varchar2(255)  NULL,
	date_field TIMESTAMP null,
	short_field NUMBER null,
	version NUMBER not NULL,
	entity_aid NUMBER  null,
	entity_bid NUMBER  null,
	entity_cid NUMBER  null,
	entity_did NUMBER  null,
    CONSTRAINT pk_test_entity PRIMARY KEY
    (
      id
    )
   ENABLE
);

/******   TestEntityA(test_entity_a) ******/
CREATE TABLE test_entity_a(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    CONSTRAINT PK_test_entity_a PRIMARY KEY
    (
	  id
    )
    ENABLE
);

/******   TestEntityB(test_entity_b) ******/
CREATE TABLE test_entity_b(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    CONSTRAINT PK_test_entity_b PRIMARY KEY
    (
	  id
    )
    ENABLE
);

/******   TestEntityC(test_entity_c) ******/
CREATE TABLE test_entity_c(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    entity_eid number  null,
    entity_fid number  null,
    CONSTRAINT PK_test_entity_c PRIMARY KEY
    (
	  id
    )
    ENABLE
);

/******   TestEntityD(test_entity_d) ******/
CREATE TABLE test_entity_d(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    CONSTRAINT PK_test_entity_d PRIMARY KEY
    (
	  id
    )
    ENABLE
);
/******   TestEntityE(test_entity_e) ******/
CREATE TABLE test_entity_e(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    entity_gid number  null,
    CONSTRAINT PK_test_entity_e PRIMARY KEY
    (
	  id
    )
    ENABLE
);
/******   TestEntityF(test_entity_f) ******/
CREATE TABLE test_entity_f(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    CONSTRAINT PK_test_entity_f PRIMARY KEY
    (
	  id
    )
    ENABLE
);

/******   TestEntityG(test_entity_g) ******/
CREATE TABLE test_entity_g(
	id number NOT NULL,
	title varchar2(255)  NULL,
	version number not NULL,
    CONSTRAINT PK_test_entity_g PRIMARY KEY
    (
	  id
    )
    ENABLE
);