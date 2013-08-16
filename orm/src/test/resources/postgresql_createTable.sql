CREATE TABLE test_entity
(
  id numeric NOT NULL,
  int_field numeric,
  string_field character varying(255),
  date_field timestamp without time zone,
  short_field numeric,
  version numeric NOT NULL,
  entity_aid numeric,
  entity_bid numeric,
  entity_cid numeric,
  entity_did numeric,
  CONSTRAINT pk_test_entity PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity
  OWNER TO postgres;



CREATE TABLE test_entity_a
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  CONSTRAINT pk_test_entity_a PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_a
  OWNER TO postgres;

  
CREATE TABLE test_entity_b
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  CONSTRAINT pk_test_entity_b PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_b
  OWNER TO postgres;


CREATE TABLE test_entity_c
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  entity_eid numeric,
  entity_fid numeric,
  CONSTRAINT pk_test_entity_c PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_c
  OWNER TO postgres;


  
CREATE TABLE test_entity_d
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  CONSTRAINT pk_test_entity_d PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_d
  OWNER TO postgres;


  CREATE TABLE test_entity_e
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  entity_gid numeric,  
  CONSTRAINT pk_test_entity_e PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_e
  OWNER TO postgres;



  
CREATE TABLE test_entity_f
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  CONSTRAINT pk_test_entity_f PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_f
  OWNER TO postgres;




CREATE TABLE test_entity_g
(
  id numeric NOT NULL,
  title character varying(255),  
  version numeric NOT NULL,
  CONSTRAINT pk_test_entity_g PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE test_entity_g
  OWNER TO postgres;