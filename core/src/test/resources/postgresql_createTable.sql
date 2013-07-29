CREATE TABLE test_table
(
  xkey numeric NOT NULL,
  name character varying(255) NOT NULL,
  version numeric NOT NULL,
  CONSTRAINT test_table_px PRIMARY KEY (xkey)
)
WITH (
OIDS=FALSE
);
ALTER TABLE test_table
OWNER TO postgres;


CREATE TABLE test_table1
(
  xbool boolean,
  "xint" numeric,
  "xbigint" numeric,
  xstring character varying(256),
  xts1 timestamp without time zone,
  xts2 timestamp without time zone,
  xblob bytea,
  xbigdecimal numeric
)
WITH (
OIDS=FALSE
);
ALTER TABLE test_table1
OWNER TO postgres;

