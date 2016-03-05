#Description 

This framework is intended to simplify working with database.

DBTool is released on a basis of spring-jdbc and provides auxiliary features.

* API for making batch select queries like  _SELECT *  FROM table WHERE field IN(?)_ with variable parameters count.
* Performant batch operations (delete, insert, update) with diagnostic of constrained violations и optimistic constrained violations for each element in the batch .
* BLOB-field API 
* Extended locking API
* Object-Relational Mapping : batch modification operations (delete/insert/update), query building, dynamic query building with filters, object relations fetching and querying.

 

##Supported databases

* **MS SQL**
* **ORACLE**
* **MySQL**
* **POSGRESQL**
* **H2**
  
##Modules

* **DBTOOL-CORE** - working with blob fields,extended applied locking, base batch operations.
* **DBTOOL-ORM** - object relation mapping implementation.

# Version 1.1

* BLOB-field positioning, ability to append data to existing field.
* Ability to build queries for ORM with  _INNER JOIN_, _LEFT JOIN_ for related objects.
* Groping related objects which are marked by  **GroupBy** annotation into associative arrays (Map).
* ORM entity can be declared without default constructor. 
* ORM: Lazy-load fetching strategy for related objects.
* ORM: Ability to fetch fields into non-ORM entity. 
* All methods that return List or Collection have to return not NULL value but empty objects.
* ORM: IFiltering API provides building of dynamic queries.
* ORM: Ability to build queries with filtering by related object fields.
* Support PostgreSQL database.
* AppLock have to support reentrant mode.
* ORM: Extended object locking with _**IEntityManager.lock**_.
* Default parameter value in в sql-predicates and **@Condition**.
* Support **AppLock** for MySQL.
* Support **BlobInputStream**, **BlobOutputStream** for MySQL.
* Refactor **IStatement** interface.
* ORM: Support component inheritance, ability to build queries to ancestor objects as bunch of _UNION_.
* Support H2 database. 
* javadocs.

#DBTOOL-CORE

##Batch select queries

One of the most efficient template of using RDBMS is batched select query with using construction like this _SELECT * FROM table WHERE field IN(?)_
This helps us to save significant count of invocation to RDBMS. So we save time for networking, sql hard and sort parsing and so on.
DBTool helps us to build such queries and map returned query result to object collections.

**REMARKS:**

Batched query building functionality can be used as it is described. But mostly it is one of the core block for building queries in **DBTOOL-ORM**.

**Implementation details:**

When query is builded by DBTool  the amount of query's parameters is equal to power of two.
So the whole amount of such queries are limited and RDBMS can make soft/hard parsing and query plans caching efficiently.

For example if we have to execute query _SELECT * FROM table WHERE id IN(?)_ with 5 parameters then DBTool builds query  _SELECT * FROM table WHERE id IN(?,?,?,?,?,?,?,?)_. Parameters values at 6-8 position will be set to NULL.

The maximum amount of parameters in block 'IN' is 1000. If we invoke query with parameters count greater than 1000, DBTool split this query into several invocation. So we have to remember that when we creating such queries, because we can receive incorrect behavior.
```java
@Resource(name="dbtool.DBTool")
private DBTool dbTool ;
public static final TestEntityRowMapper LIST_ROW_MAPPER = new TestEntityRowMapper();
 
public static final class TestEntityRowMapper implements RowMapper<TestEntity> {
        public TestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version"));
        }
    }
 
 
private void select(){ 
   List<Integer> list = Arrays.asList(1,2,3,4,5);
    List<TestEntity> result = dbTool.selectList("SELECT xkey,name,version FROM test_table WHERE xkey IN(?) order by xkey", LIST_ROW_MAPPER, list);
}
```

**Query result types**

We can get different collection types if we need it.

Result|API|	Description
---------|---|----------
List<T>|DBTool.selectList(String selectSQL, Class<T> cls, Object... inValues)|maps result to  Class<T> according for the rules of spring-jdbc
Set<T>|DBTool.selectSet(String selectSQL, RowMapper<T> rowMapper, Object... inValues)|we want to receive non-repeatable collection of elements
Set<T>|DBTool.selectSet(String selectSQL, Class<T> cls, Object... inValues)|maps result to  Class<T> according for the rules of spring-jdbc
Map<K, List<V>>|DBTool.selectMapList(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues)|puts result in associative array.  RowMapper has to return "key-value" pair
Map<K0, Map<K, V>>|DBTool.selectMapOfMaps(String selectSQL, RowMapper<KeyValue<K0, KeyValue<K, V>>> rowMapper,Object... inValues)|"exotic" grouping of  result
Map<K, V>|DBTool.selectMap(String selectSQL, RowMapper<KeyValue<K, V>> rowMapper, Object... inValues)|puts result in associative array.  RowMapper has to return "key-value" pair

```java
@Resource(name="dbtool.DBTool")
private DBTool dbTool ;
public static final TestEntityMapRowMapper MAP_ROW_MAPPER = new TestEntityMapRowMapper();
public static final TestEntityMapOfMapRowMapper MAP_OF_MAP_ROW_MAPPER = new TestEntityMapOfMapRowMapper();
  
public static final class TestEntityMapRowMapper implements RowMapper<KeyValue<String, TestEntity>> {
        public KeyValue<String, TestEntity> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<String, TestEntity>(rs.getString("name"),
                    new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version")));
        }
 }
 
public static final class TestEntityMapOfMapRowMapper implements RowMapper<KeyValue<String, KeyValue<Integer, TestEntity>>> {
        public KeyValue<String, KeyValue<Integer, TestEntity>> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<String, KeyValue<Integer, TestEntity>>(rs.getString("name"),
                    new KeyValue<Integer, TestEntity>(rs.getInt("xkey"),
                            new TestEntity(rs.getInt("xkey"), rs.getString("name"), rs.getInt("version"))));
        }
}
  
  
private void selectMap(){
   List<Integer> list = Arrays.asList(1,2,3,4,5);
   List<TestEntity> result = dbTool.selectMap("SELECT xkey,name,version FROM test_table WHERE xkey IN(?) AND name=? order by xkey", LIST_ROW_MAPPER, list,"n01");
}
 
private void selectMapList(){
   List<Integer> list = Arrays.asList(1,2,3,4,5);
   Map<String, List<TestEntity>> mapList = dbTool.selectMapList("SELECT xkey,name,version FROM test_table WHERE xkey IN(?)", MAP_ROW_MAPPER, list);
}
 
private void selectMapOfMap(){
 HashSet<Integer> keys = new HashSet<Integer>();
 keys.add(2);
 keys.add(5);
 keys.add(6);
 keys.add(8);
 
 HashSet<String> names = new HashSet<String>();
 names.add("n_0");
 names.add("n_1");
 names.add("n_2");
 
  Map<String, Map<Integer, TestEntity>> map = dbTool.selectMapOfMaps("SELECT xkey,name,version FROM test_table WHERE name IN(?) AND xkey IN(?) order by xkey", MAP_OF_MAP_ROW_MAPPER, names, keys);
}
```
**Nuances:**

* Queries with _IN(?)_ works only with  _NOT NULL_ fields . Otherwise, if the field can contain NULL value, we have to extend query like this: _SELECT * FROM table WHERE field IN(?) AND field IS NOT NULL_.
* Your query can contain several _IN(?)_ conditions. But you have to be sure that only one block can contain more than 1000 parameters. Otherwise you can obtain incorrect result.
* You have to use  **FieldGetter** for column value extraction from **ResultSet** inside your **RowMapper**. 
* If you need to map parameter to concrete RDBMS data type you must wrap parameter value into **org.springframework.jdbc.core.SqlParameterValue**, and  for collection parameter use **ru.kwanza.dbtool.core.SqlCollectionParameterValue**.

##Batched modification operations

For OLTP systems that make massive journaling of incoming data, execution of modification queries can be a common bottleneck. If your application try to make this queries one by one - each of them will be a competitor for the same resources: database transaction log, database files, indexes and so forth. 
We can avoid this bottleneck if we will use batched modification operations and execute them with using **java.sql.Statement.addBatch**. 

**Benefits of this approach:**

* we reduce networking with RDBMS.
* query is executed in one database transaction scope, so we reduce concurrency on database transaction log and concurrency on **javax.transaction.TransactionManager** in JEE container, if we have it.

The only significant  problem is to loose the whole batch if our transaction is rolled back.
DBTool helps to solve this problem. It offer possibility to determine list of elements, which were failed for the reasons like ***constrained violation*** and **optimistic constrained**. After that the arrival of a decision is made by developer in each particular cases.

**REMARKS:**

Batched query building functionality can be used as it is described. But mostly, it is one of the core block for **DBTOOL-ORM**.

###Insert operation
For this kind of operations we should use method:

```java
public <T> long DBTool.update(String updateSQL, final Collection<T> objects, final UpdateSetter<T> updateSetter) throws UpdateException 

```
This method returns the record count that were removed/updated:

* **UpdateException.<T>getConstained()** -  contains list of elements with constrained violation.
* **UpdateSetter.setValue** - if we want to skip element from updating/removing we have to return false.

```java
@Resource(name="dbtool.DBTool")
private DBTool dbTool ;
public static final TestEntityUpdateSetter TEST_BATCHER = new TestEntityUpdateSetter();
public static final TestEntityUpdateSetter TEST_BATCHER_WITH_SKIP = new TestEntityUpdateSetterWithSkip ();
 
protected static class TestEntityUpdateSetter implements UpdateSetter<TestEntity> {
        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            FieldSetter.setInt(pst, 3, object.getVersion());
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 1, object.getKey());
            return true;
        }
}
 
protected static class TestEntityUpdateSetterWithSkip implements UpdateSetter<TestEntity> {
 
        public boolean setValues(PreparedStatement pst, TestEntity object) throws SQLException {
            // skip test element
            if(object.getName().startWith("test")) return false;
            FieldSetter.setInt(pst, 3, object.getVersion());
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 1, object.getKey());
            return true;
        }
 }
 
public void insert() throws UpdateException{
   List<TestEntity> testEntities = new ArrayList<TestEntity>();
   for (int i = 0; i < 10; i++) {
          testEntities.add(new TestEntity(11 + i, "Element" + (11 + i), 0));
   }
    // insert 10 elements in one statement
   dbTool.update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER));
}
 
public void insertWithTestSkip() throws UpdateException{
   List<TestEntity> testEntities = new ArrayList<TestEntity>();
   for (int i = 0; i < 10; i++) {
          testEntities.add(new TestEntity(11 + i, "Element" + (11 + i), 0));
   }
 
   for (int i = 10; i < 15; i++) {
          testEntities.add(new TestEntity(11 + i, "testElement" + (11 + i), 0));
   }
 
   // insert only 10 elements and skip all with name started with "test"
   dbTool.update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER_WITH_SKIP ));
}
 
 
pubic void insertWithConstrained(){
   List<TestEntity> testEntities = new ArrayList<TestEntity>();
   for (int i = 0; i < 10; i++) {
          testEntities.add(new TestEntity(11 + i, "Element" + (11 + i), 0));
   }
   for (int i = 1; i < 10; i++) {
      testEntities.add(new TestEntity(11 + i, "Element" + (11 + i), 0));
   }
   for (int i = 20; i < 30; i++) {
      testEntities.add(new TestEntity(11 + i, null, 0));
   }
    // insert only 10 first elements in one statement
    // we have 10 constrained on id unique key 
    // and 10 constrained on field name, that is not nullable 
    try{
       dbTool.update("insert into test_table(xkey,name,version) values(?,?,?)", testEntities, TEST_BATCHER));
    }catch(UpdateException e){
       System.out.println("Constrained " + e.<TestEntity>getConstrainted().size());// Contrained 20
    }
}
```

###Update and Remove Operations
In high-load OLTP systems optimistic locking is the only possible choice because of pessimistic strategy requires to much resources. So DBTool helps to implement it firstly.
DBTool requires entities to have  special field "version" of type LONG

For update and remove operation we have following method in API: 

```java
public <T, K extends Comparable, V> long DBTool.update(String updateSQL, Collection<T> objects, UpdateSetterWithVersion<T, V> updateSetter,String checkSQL, RowMapper<KeyValue<K, V>> keyVersionMapper, FieldHelper.Field<T, K> keyField, FieldHelper.VersionField<T, V> versionField)

```

Where:

* **updateSQL** - query like _UPDATE table SET field1=?,  field2=?,  field3=? ,  version=? WHERE id=? and version=?_.
* **objects** - collection of object to be updated.
* **UpdateSetterWithVersion** - mapping of object field and new/old value of version field. If you want to skip object from updating you have to return _false_ from _setValue_.
* **checkSQL** - query like  _SELECT  id, version FROM  test_table WHERE ID IN(?)_.
* **keyVersionMapper** - mapping for query _checkSQL_ 
* **keyField** - primary key extractor from object.
* **versionField** - version field extractor. You should better use **ru.kwanza.dbtool.core.VersionGenerator** for version generation.

```java
@Resource(name="dbtool.DBTool")
private DBTool dbtool ;
@Resource(name="dbtool.VersionGenerator")
private VersionGenerator generator;
 
public static final FieldHelper.Field<TestEntity, Integer> KEY = new FieldHelper.Field<TestEntity, Integer>() {
     public Integer value(TestEntity object) {
         return object.key;
     }
};
 
public static final FieldHelper.VersionField<TestEntity, Integer> VERSION = new FieldHelper.VersionField<TestEntity, Integer>() {
     public Integer value(TestEntity object) {
         return object.version;
     }
     public Integer generateNewValue(TestEntity object) {
         return generator.generate("TestEntity",object.getVersion());
     }
     public void setValue(TestEntity object, Integer value) {
         object.version = value;
     }
};
 
 
public static final TestEntityUpdateSetter TEST_BATCHER = new TestEntityUpdateSetter();
protected static class TestEntityUpdateSetter6 implements UpdateSetterWithVersion<TestEntity, Integer> {
        public boolean setValues(PreparedStatement pst, TestEntity object, Integer newVersion, Integer oldVersion) throws SQLException {
            FieldSetter.setInt(pst, 1, newVersion);
            FieldSetter.setString(pst, 2, object.getName());
            FieldSetter.setInt(pst, 3, object.getKey());
            FieldSetter.setInt(pst, 4, oldVersion);
            return true;
        }
}
 
public static final KeyVersionRowMapper KEY_VERSION_MAPPER = new KeyVersionRowMapper();
private static final class KeyVersionRowMapper implements RowMapper<KeyValue<Integer, Integer>> {
        public KeyValue<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new KeyValue<Integer, Integer>(rs.getInt("xkey"), rs.getInt("version"));
        }
}
 
public void update() throws UpdateException{
   List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table", LIST_ROW_MAPPER);
   for(TestEntity t : testEntities){
      t.setName("New name");
   }
 
   dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER,
                 "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, KEY,VERSION));
}
 
public void update(){
   List<TestEntity> testEntities = getDBTool().selectList("select xkey,name,version from test_table", LIST_ROW_MAPPER);
   for(TestEntity t : testEntities){
      t.setName("New name");
   }
   try{
      dbTool.update("update test_table set version=? ,name=? where xkey=? and version=?", testEntities, TEST_BATCHER,
                    "select xkey,version from test_table where xkey in(?)", KEY_VERSION_MAPPER, KEY,VERSION));
   }catch(UpdateException e){
      System.out.println(e.<TestEntity>getConstrained().size()); // object with constrained violation
      System.out.println(e.<TestEntity>getOptimistic().size()); // object with optimistic constrained (version was changed by other process)
   }
}
```
**Nuances:**

* You have to use  *FieldSetter* class for columns setting

##BLOB-field API

Sometimes we need to save large bunch of information in database. Fields of BLOB type fit for that thoroughly. DBTool helps to exploit this fields like accustomed streams( *InputStream* and *OutputStream*), makes this transparently and in the most efficient way for each type of supported RDBMS because of it relies on inner features of that databases, that are not exposed in public JDBC interfaces.

DBTool provides following API methods:

```java
// construct input stream for reading from some field of some table
public BlobInputStream getBlobInputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
          throws IOException {
      return BlobInputStream.create(this, tableName, fieldName, conditions);
  }
// construct output stream for writing 
public BlobOutputStream getBlobOutputStream(String tableName, String fieldName, Collection<KeyValue<String, Object>> conditions)
          throws IOException {
      return BlobOutputStream.create(this, tableName, fieldName, conditions);
 }
```

**BlockInputStream** supports partial reading and stream positioning 

* **getSize()** - the size of the BLOB field.
* **skip(int n)** -  skips  n bytes.
* **getPosition()** - get current position for reading.

When you use **BlobOutputStream you should remember followings:

1. **BlobOutputStream** is created in "append" mode for default. In other words , the current writing position is pointing at  the end of the BLOB-field.
1. If you want to change some  bytes in the BLOB-field, you have to use _**BlobOutputStream.setPosition**_
1. If you want to make  BLOB-field empty then you must use _**BlobOutputStream.reset**_ method

```java
//write  "hello"
BlobOutputStream blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
blobOS.write("hello".getBytes());
blobOS.close();
 
//append " world"
blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
blobOS.write(" world".getBytes());
blobOS.close();
 
//change first character of the words  to upper case
blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
blobOs.setPosition(1);
blobOS.write("H".getBytes());
blobOs.setPosition(7);
blobOS.write("W".getBytes());
blobOS.close();
 
//read result and check
BlobInputStream blob =
                getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
long size = blob.getSize();
byte[] b = new byte[11];
blob.read(b);
blob.close();
assertEquals(size, 11);
assertEquals(new String(b), "Hello World");
 
//make field empty
blobOS = getDBTool().getBlobOutputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
        blobOS.reset();
        blobOS.close();
 
//read and check result
blob =getDBTool().getBlobInputStream("test_blob", "value", Arrays.asList(new KeyValue<String, Object>("id", 1)));
size = blob.getSize();
blob.close();
assertEquals(size, 0);
```
##How to use

The better way to  use framework is to include it as maven dependency

**Maven Config:**
```xml
<dependency>
   <groupId>ru.kwanza.dbtool</groupId>
   <artifactId>dbtool-core</artifactId>
   <version>${dbtool.version}</version>
</dependency>
```
**Spring config:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
 
    <import resource="classpath:dbtool-config.xml"/>
 
</beans>
```

After including context you can use following beans:

* **dbtool.DBTool** - this is the main utility ru.kwanza.dbtool.core.DBTool
* **dbtool.VersionGenerator** - ru.kwanza.dbtool.core.VersionGenerator

This beans require you to introduce  bean:

* **datasource** - javax.sql.DataSource which is responsible for database connection

#DBTOOL-ORM

**DBTOOL-ORM** helps to do followings:

1. Declare field-to-column mappings.
1. Execute CRUD operations for entities.
1. Support batch DML with processing constrained violation and optimistic constrained.
1. Build dynamic queries with sql-predicates ( _=_ , _<>_ , _>_ , _<_ , _<=_ , _>=_ , _LIKE_ , _IN_ , _BETWEEN_ , _IS NULL_ , _IS NOT NULL_ ).
1. Build queries with relations: _INNER JOIN_, _LEFT JOIN_.
1. Build queries with sorting by field and/or by field of related entity.
1. Design complex entity hierarchy; support building queries for abstract base entity.
1. Execute native db queries with parameters.
1. Support different strategy for loading related queries.
1. Build dynamic queries on top of filter concept; this is very useful for building controllers that have to return data which is filtered and sorted by different columns. 
1. Support advanced entity locking strategies.

Why do you need to use this framework

1. It simplify developing of DAO-level of your application (like hibernate, toplink , openjpa  and etc.) and improve performance of OLTP application because all operations is batched.
1. It offers nice functionality to build server-side data controller level. You can easily build controller which is responsible for data selection by different criteria.
1. It offers resilient approach for entities relations manipulations. For every concrete case you can choose how to fetch related entities.

##Field-to-column mappings

All ORM entities have to be marked with annotation:
**@ru.kwanza.dbtool.orm.annotaions.Entity**
Where:

* **name** - entity name (optional).
* **sql** - optional parameters, you need it if you want to map your read-only entity to some database sql-query.
* **table** - table name.

After that you have to mark key entity's field with annotation **@ru.kwanza.dbtool.orm.annotations.IdField** (DBTool doesn't support composite keys yet)

Where:

* **value** - column name.
* **type** - column type (optional), choose one from javax.sql.Types if you need.

After that, if you are going to use optimistic strategy you have to specify version field using **@ru.kwanza.dbtool.orm.annotations.VersionField** annotation. The type of this field must be **Long**.

Where:

* **value** - column name.

Further, you use annotation **@ru.kwanza.dbtool.orm.annotations.Field** to mark ordinary fields.

Where:

* **value** - column name.
* **type** - column type(optional). Use one from javax.sql.Types if you need it.

Any of field annotations can be applied either for entity field or for "getter"-method.
```java
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity {
    @IdField(column = "id")
    private Long id;
    @Field(column = "int_field")
    private Integer intField;
    @Field(column = "string_field")
    private String stringField;
    @Field(column = "date_field")
    private Date dateField;
    @Field(column = "short_field")
    private Short shortField;
    @VersionField(column = "version")
    private Long version;
 
 
}
 
@Entity(name = "TestEntity1", table = "test_entity")
public class TestEntity1 extends Agent {
    @IdField("id")
    private Long id;
    @Field("name")
    private String name;
    @Field(value = "desc", type = Types.NVARCHAR)
    private String description;
    @VersionField("version")
    private Long version = 0l;
    private Long counter;
 
 
    @Field("counter")
    public Long getCounter() {
        return counter;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
````

**REMARKS:**

* You can can map some entities to one table. It helps to use different fieldset for different cases.
* All annotated fields/methods are accessible for descendants. So you can use inheritance.
* If you use  **@ru.kwanza.dbtool.orm.annotations.Field** for getter-method, you have to declare also a setter-method for this property

## CRUD-operations
**ru.kwanza.dbtool.orm.api.IEntityManager** helps to make CRUD(create,read,update,delete) operations.

Execution of modification operations can be done in batch mode differently:

* You can invoke methods _**create**_, _**update**_, _**delete**_  of **IEntityManager** with collections of objects.
* You can use **IEntityBatcher** and  IEntityManger.newBatcher**, which help to accumulate object's operations and then flush them as a batch-operation.

```java
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
 
    public TestEntity(long id, String title) {
       this.id = id;
       this.title = title;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
}
 
 
 
 
public class TestEntityDAO{
  @Resource(name="dbtool.IEntityManager")
  private IEntityManager  em;
 
 
  public TestEntity createEntity(long id, String title){
     return em.create(new TestEntity(id,title);
  }
 
  public TestEntity update(long id, String newTitle){
     TestEntity entity = em.readById(TestEntity.class,id);
     entity.setTitle(newTitle);
     return em.update(entity);
  }
 
  public TestEntity delete(long id){
     TestEntity entity = em.readById(TestEntity.class,id);
     return em.delete(entity);
  }
 
  public void batchGenerate(List<String> titles){
     IEntityBatcher batcher = em.newBatcher();
 
     for(String t: title){
        batcher.create(new TestEntity(generateID(),t);
     }  
 
     batcher.flush();
  }
 
}
````

**REMAKRS:**

* **VersionField** should be better declared as private field. You have to restrict user access to this field, because of only **IEntityManager** responsible to process optimistic locking strategy.
* You can handle **constrained violation** and  **optimistic constrained** by catching **EntityUpdateException**, which is analogous for **UpdateException** in **DBTOOL-CORE**

##Query Building

For searching entities you can use queries, DBTool provide special API for building queries in **ru.kwanza.dbtool.orm.api.IQueryBuilder** class.

When we build query and declare some sql-predicate or sorting order we have to use field name (NOT table column names!).

**IQueryBuilder** provides methods:

* _**when**_ - specify query conditions with **ru.kwanza.dbtool.orm.api.If**.
* _**orderBy**_ - specify ordering.
* _**createNative**_ - create custom native db query if you need something complex. You can use symbol _?_ to mark parameter, if you want index-based access, or text _:<paramName>_ if you want name-based access. 

**IQuery** objects should be better cached, it helps to improve you performance and skip query building phase(it is like named queries in Hibernate)

In order to execute query you have to construct  **IStatement** object with using _**IQuery.prepare()**_.

After that you specify parameters values with _**IStatement.setParameter**_.

**IStatement** provides methods:

* _**paging(offset, size)**_ - set offset and size of queries data. This method helps to implement data pagination and uses proprietary database construction for efficiency.
* _**select**_ - select single object. If your query returns more than one object you will receive  **org.springframework.dao.IncorrectResultSizeDataAccessException**.
* _**selectList**_ - select list of objects.
* _**selectMap**_ - select objects that are grouped by specified object property value.
* _**selectMapList**_ -  select map of objects list that are grouped by specified property value.

```java
public class TestEntityDao{
  @Resource(name="dbtool.IEntityManager")
  private IEntityManager em;
  private IQuery query1;
  private IQuery query2;
  private IQuery query3;
 
 
  public void init(){
    query1 = em.queryBuilder(TestEntity.class)
                .where(If.and(
                        If.or(Condition.in("id"),
                                If.like("field1"),
                                If.isEqual("field2"),
                                If.isGreater("field3")),
                        If.or(
                                If.isGreaterOrEqual("field2"),
                                If.isLess("field3"),
                                If.isLessOrEqual("field4"),
                                If.isNotNull("field5"),
                                If.isNull("field6")),
                        If.between("date"),
                        If.or(
                                If.notEqual("field1"),
                                If.notEqual("field2"))
                ))
               .orderBy(OrderBy.ASC("id"))
               .orderBy("title DESC")).create();
  
     query2 = em.queryBuilder(TestEntity.class)
              .where(If.or(If.in("id","ids");
     query3 = em.queryBuilder(TestEntity.class)
              .createNative("SELECT * FROM test_entity_1 where id in(:id) " +
                            "UNION ALL SELECT * FROM test_entity_2 where id in(:id)");
     
  }
 
  public Collection<TestEntity> read1(...){ 
    return query1.prepare() 
           .setParameter(1,Arrays.asList(1,2,3,4,5,6,7,8,9))
           .setParameter(2,"test%")
           .setParameter(3,32)
           .setParameter(4,10)
           .setParameter(5,200)
           .setParameter(6,1000)
           .setParameter(7,2000)
           .setParameter(8,1300)
           .setParameter(9,1500)
           .setParameter(10,"test_2")
           .selectList();
  }
 
  public Collection<TestEntity> read2(...){ 
    //прочитать первые 100 записей
    return query2.prepare() 
           .paging(0,100)
           .setParameter("ids",Arrays.asList(1,2,3,4,5,6,7,8,9))
           .selectList();
  }

  public Collection<TestEntity> read3(...){ 
    //прочитать первые 100 записей
    return query2.prepare() 
           .paging(0,100)
           .setParameter("ids",Arrays.asList(1,2,3,4,5,6,7,8,9))
           .selectList();
  }
 
  public Map<Long,TestEntity> read4(...){
     return query3.prepare() 
           .setParameter("ids",Arrays.asList(1,2,3,4,5,6,7,8,9))
           .selectMap("id");
  }

  public Map<String,List<TestEntity>> read5(...){
     return query3.prepare() 
           .setParameter("ids",Arrays.asList(1,2,3,4,5,6,7,8,9))
           .selectMapList("field1");
  }
}
````

##Relations

For reading entity relations DBTool uses on-demand approach. All relation mappings are used only for convenience of reading related entities. In other words, they are read-only and they are  always provided by field-to-column mapping.

You can use one of the following annotations:

* **ru.kwanza.dbtool.orm.annotations.OneToMany** - relation of type "one-to-many".
* **ru.kwanza.dbtool.orm.annotations.ManyToOne**- relation of type "many-to-one".
* **ru.kwanza.dbtool.orm.annotations.Association** - common case of relation where entities are linked by arbitrary fields, not necessary id-field.

 
Annotation **@ OneToMany** has following fields:

* **relativeProperty** -  field name of related entity.
* **relativeClass** - class of related entities, is used when field type is Collection.

Following example shows how to establish one-to-many relation between _test_entity_a.id ->test_entity.entity_aid_ :

```java
@Entity(name="TestEntityA", table = "test_entity_a")
public class TestEntityA {
    @IdField("id")
    private Long id;
   
    // actually "entityAID" points to TestEntity.entityAID which has field-to-column mapping
    @OneToMany(relationClass = TestEntity.class,relationProperty = "entityAID")
    private Collection<TestEntity> testEntities;
}
 
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity {
    @IdField("id")
    private Long id;
    
    @Field("entity_aid")
    private Long entityAID;  
}
````

Annotation **@ ManyToOne** has following parameters:

* **property** -  field name that is linked two entities.

Following example shows how to establish one-to-many relation between _test_entity_a.id ->test_entity.entity_aid_ :

```java
@Entity(name="TestEntityA", table = "test_entity_a")
public class TestEntityA {
    @IdField("id")
    private Long id; 
}
 
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity {
    @IdField("id")
    
    @Field("entity_aid")
    private Long entityAID; 
 
    //"entityAID" points to TestEntity.entityAID which has field-to-column mapping
    @ManyToOne(property="entityAID")
    private TestEntityA entityA;
}
````
Method _**IEntityManager.fetch**_ helps to read relations for existing objects.
One of the method's parameters is string that describes which relations do you want to read. Besides, this parameter can contain path to inner related objects.

Format of this parameter: 

_**"property1, property2, property3 {property31 ,property32 {property321}}, property33}, property4"**_

This example shows us:

* entities has fields _property1_, _property2_, _property3_ and _property4_ that are marked by on of **@OneToMany** / **@ ManyToOne** / **@ Association** .
* entity in field _property3_  has fields _property31_, _property32_, _property33_ that are marked by on of **@ OneToMany** / **@ ManyToOne** / **@ Association**. 
* entity in field _property3_  has field _property321_ which is marked by one of **@OneToMany** / **@ ManyToOne** / **@ Association**.

```java
@Entity(name = "TestEntity", table = "test_entity")
public class TestEntity {
    @IdField("id")
    private Long id;
    @VersionField("version")
    private Long version;
    @Field("entity_aid")
    private Long entityAID;
    @Field("entity_bid")
    private Long entityBID;
    @Field("entity_cid")
    private Long entityCID;
    @Field("entity_did")
    private Long entityDID;
 
    @ManyToOne(property = "entityAID")
    private TestEntityA entityA;
    @ManyToOne(property = "entityBID")
    private TestEntityB entityB;
    @ManyToOne(property = "entityCID")
    private TestEntityC entityC;
    @ManyToOne(property = "entityDID")
    private TestEntityD entityD;
}
 
@Entity(name="TestEntityA", table = "test_entity_a")
public class TestEntityA {
    @IdField("id")
    private Long id;
    @Field(column = "title")
    private String title;
    @VersionField("version")
    private Long version;
 
}
 
@Entity(name="TestEntityB", table = "test_entity_b")
public class TestEntityB {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField(column = "version")
    private Long version;
}
 
@Entity(name = "TestEntityC", table = "test_entity_c")
public class TestEntityC {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
 
    @Field("entity_eid")
    private Long entityEID;
    @Field("entity_fid")
    private Long entityFID;
 
    @ManyToOne(property = "entityEID")
    private TestEntityE entityE;
    @ManyToOne(property = "entityFID")
    private TestEntityF entityF;
}
 
@Entity(name = "TestEntityD", table = "test_entity_D")
public class TestEntityD {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
}
 
@Entity(name = "TestEntityE", table = "test_entity_e")
public class TestEntityE {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
    @Field(column = "entity_gid")
    private Long entityGID;
    @ManyToOne(property = "entityGID")
    private TestEntityG entityG;
}
 
@Entity(name="TestEntityF", table = "test_entity_f")
public class TestEntityF {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
}
 
 
@Entity(name = "TestEntityG", table = "test_entity_g")
public class TestEntityG {
    @IdField("id")
    private Long id;
    @Field("title")
    private String title;
    @VersionField("version")
    private Long version;
}
 
 
public class TestEntityDao{
    @Resource(name = "dbtool.IEntityManager")
    private IEntityManager em;
    private IQuery searchEntities;
 
    public void init(){
      this.searchEntities = em.queryFor(TestEntity.class)...create();
    }
 
    public Collection<TestEntity> getEntities1() {
        List<TestEntity> testEntities = query.selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "entityA,entityB,entityC{entityF,entityE{entityG}},entityD"); 
        return testEntities
    }  
 
    public Collection<TestEntity> getEntities1() {
        List<TestEntity> testEntities = query.selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "entityA,entityB,entityC{entityF,entityE},entityD");
        return testEntities
    }  
 
    public Collection<TestEntity> getEntities1() {
        List<TestEntity> testEntities = query.selectList();
        em.getFetcher().fetch(TestEntity.class, testEntities, "entityA, entityB, entityC, entityD");
        return testEntities
    } 
}
````

**REMARKS:**

* Framework user have to think about cyclic relations by himself
* As regards the implementation, all related entities are read by queries like _SELECT * FROM WHERE ID IN(?)_
* Framework caches queries for relations, so they are very performant

##Building queries with interceptions: _INNER JOIN_, _LEFT JOIN_

In some cases queries with interceptions can be more efficient, especially for entities with low cardinality.
Framework provides ability to build such queries using method _**IQueryBuilder.join**_.

Types of join:

* **Join.Type.INNER** - related entity is added to query using  _INNER JOIN_
* **Join.Type.LEFT** - related entity is added to query using _LEFT JOIN_
* **Join.Type.FETCH** - related entity will be read by separate query like _SELECT * FROM WHERE ID IN(?)_.

You can use nested relation also in _**IQueryBuilder.join**_ .

Example:

```java
@Entity(name="Contract", table="contract")
public class Contract{
  ...
  @ManyToOne(property = "agentId")
  private Agent agent;;
  
}
 
@Entity(name="Agent", table="agent")
public class Agent{
  ...
  @ManyToOne(property = "parentAgentId")
  private Agent parentAgent;
  @OneToMany(relationProperty = "id")
  private AgentDetail agentDetail;
  @ManyToOne(property = "agentCategoryId")
  private AgentCategory agentCategory;
 
}
 
...
 
IQuery<Contract> q1 = em.queryBuilder(Contract.class)
                    .join(Join.left("Agent",Join.inner("agentDetail"),Join.inner("agentCategory"))).create();
````

For this example we get following query:

```sql
SELECT * FROM contract LEFT JOIN (agent INNER JOIN agent_detail ON agent.id=agent_detail.id INNER JOIN agent_category on agent.category_id=agent_category.id) ON contract.agent_id=agent.id
````

We can build complex **IQuery** objects with using **Join.Type.FETCH**  that make several invocation to RDBMS 

```java
IQuery<Contract> q2 = em.queryBuilder(Contract.class).join(Join.fetch("Agent",Join.inner("agentDetail"),Join.fetch("agentCategory"))).create();
````

For this example we make 3 sql-invocations:

```sql
# read  Contract
select * from contract;
#fetch relations Contract.agent, Contract.agent.agentDetail by one query
select  * from agent INNER JOIN agent_detail ON ... WHERE agent.id in(?)
#fetch relation Contract.agent.agentCategory
select * from agent_category WHERE agent_category.id in(?)
````

Type of intersection can be specified directly in string parameter of method **IQueryBuilder.join**, that describes relations path.  

_**"(!|&|)property1, (!|&|)property2, (!|&|)property3 {(!|&|)property31 ,(!|&|)property32 {(!|&|)property321}}, (!|&|)property33}, (!|&|)property4"**_, 

Where

* **&** - is for _LEFT JOIN_
* **!** - is for _INNER JOIN_
* **(empty)** - related entity is fetched by separate query,in other words it is **FETCH JOIN**

Previous two queries can be overwritten:

```java
IQuery<Contract> q1 = em.queryBuilder.join("&agent{!agentDetail, !agentCategory)").create();
IQuery<Contract> q2 = em.queryBuilder.join("agent{!agentDetail, agentCategory)").create();
````

This approach you can also use in method **IEntityManager.fetch**:

```java
IEntityManager em = ...;
IQuery<Contract> query = em.queryBuilder(Contract.class).create();
List<Contract> result = query.prepare().selectList();
em.fetch(Contract.class, "agent{!agentDetail, agentCategory}");
````
There will be 3 sql-queries for this example:

```sql
# read  Contract
select * from contract;
#fetch relations Contract.agent, Contract.agent.agentDetail
select  * from agent INNER JOIN agent_detail ON ... WHERE agent.id in(?)
#fetch relation Contract.agent.agentCategory
select * from agent_category WHERE agent_category.id in(?)
````

If you use  sql-predicate or sorting with field of related query then framework adds intersection automatically.

```java
IQuery<Contract> q = em.queryBuilder(Contract.class).where(If.like("agent.title")).orderBy("agentDetail.sum 
DESC").create()
````

which is equivalently to :

```sql
SELECT * FROM contract INNER JOIN agent ON ... INNER JOIN agent_detail ON ... WHERE agent.title = ? ORDER BY anget_detail.sum DESC;
````

##Lazy load of related entities

In some cases it is more convenient to load related entities directly when you inquire the field value.
This simplifies  DAO-level, especially when we have many relations.

**IEntityManager** provides following method:

```java
public interface IEntityManager{
    ...
 
   <T> void fetchLazy(Class<T> entityClass, Collection<T> items);
 
    ...
}
````

When you invoke this method for all fields in **items** that represent relations, framework generates special proxy-objects. Reading of related objects will be done  after invocation of any method in this proxy-object. And of cause  all this will be done by batch select queries.

Example:

```java
IEntityManager em = ...;
List<Contract> items = query.prepare().execute();
em.fetchLazy(Contract.class,items);
 
for(Contract c: items){
   Agent a = c.getAgent();
   System.out.println("Agent title:" + agent.getTitle()); // will be made ONE query to fetch relation: SELECT * FROM agent WHERE id in(?)
   AgentDetail ad = a.getAgentDetail();
   System.out.println("Agent debSum:" + ad.getDebSum()); // will be made ONE query to fetch relation: SELECT * FROM agent_details WHERE id in(?)
}
````
It is important to understand that you have to use special method to check if relation is missing. It's **IEntityManager.isNull**, because of proxy-object doesn't now anything about existence before query will be made.

**WRONG**

```java
for(Contract c: items){
    if(c.getAgent()!=null){ 
     System.out.println(agent.getTitle()); 
     if(c.getAgent().getAgentDetail()!=null){
        System.out.println(c.getAgent().getAgentDetail().getDebtSum());
     }    
   }  
}
````

**RIGHT**

```java
for(Contract c: items){
    if(!em.IsNull(c.getAgent())){
     System.out.println(agent.getTitle());
     if(!em.isNull(c.getAgent().getAgentDetail())){
        System.out.println(c.getAgent().getAgentDetail().getDebtSum());
     }    
   }  
}
````

Another approach to mark relations as lazy-load is to use IQueryBuilder.lazy():

```java
IQuery<Contract> q = em.queryBuilder(Contact.class).join("!agent").lazy().create();
List<Contract> items  = q.prepare().selectList(); //execute SELECT * from contract INNER JOIN agent ON ...
 
for(Contract c: items){
   Agent a = c.getAgent();
   System.out.println("Agent title:" + agent.getTitle());
   AgentDetail ad = a.getAgentDetail();
   System.out.println("Agent debSum:" + ad.getDebSum()); // will be made ONE query to fetch relation: SELECT * FROM agent_details WHERE id in(?)
}
````

So, as you can see, we can mix IQueryBuilder.lazy() and  IQueryBuillder.join(). Some relations will be read immediately but for others we can  use lazy-load mode.

Generated proxy-objects have interesting features:

* they are serializable
* you can use them outside current database transaction (in contrast of  hibernate)

This features made them more useful unlike  the same in hibernate.

##Entity inheritance 

Compose entities hierarchy is a common task for  ORM.
DBTool provides only one approach when each descendant is stored in separate table.

You have to mark the root entity with **@AbstractEntity** annotation. 

Example:

```java
@AbstractEntity(name="Trx"
public class Trx{
    @IdField("id")
    private Long id;
    @Field("result_code")
    private Integer resultCode;
    @Field("started_at"
    private Date startedAt;
    @Field("finished_at")
    private Date finishedAt; 
}
 
@AbstractEntity(name="PaymentTrx")
public class PaymentTrx extends Trx{
  @Field("amount")
  private Long amount;
}
 
@Entity(name="OnlinePaymentTrx", table="online_payment_trx")
public class OnlinePaymentTrx extends PaymentTrx{
   ...
}
 
@Entity(name="OfflinePaymentTrx", table="offline_payment_trx")
public class OnlinePaymentTrx extends PaymentTrx{
   ...
}
 
@Entity(name="GetKeysTrx", table="get_keys_trx")
public class GetKeysTrx extends Trx{
  ...
}
````

Concrete entity types with can be  create, update, delete are OnlinePaymentTrx,OfflinePaymentTrx,GetKeysTrx, and all the rest are readonly entities.

Examples:

```java
IEntityManager em = ...;
IQuery<Trx> q1 = em.queryBuilder(Trx.class).where(If.isEqual("resultCode",If.valueOf(1)).create();
IQuery<Trx> q2 = em.queryBuilder(PaymentTrx.class).where(If.isGreater("amount",If.valueOf(10000l)).create();
 
List<Trx> items = q1.prepare().selectList()
for(Trx t : items){
   if(t instanceOf GetKeysTrx){
      processGetKeys();
   }else if(t instanceOf OnlinePaymentTrx){
      processOnlinePayment();
   }else if(t instanceOf OfflinePaymentTrx){
     processOfflinePayment();
   }else{
      throw IllegalStateExpception("Unknown entity type!"); //must not be ever thrown   
   }
}
````

this is equivalent to :

```sql
# execute q1
SELECT * FROM (SELECT * FROM get_keys_trx 
               UNION ALL 
               SELECT * FROM online_payment_trx 
               UNION ALL 
               SELECT * FROM offline_payment_trx) 
WHERE result_code=1
 
#execute q2
SELECT * FROM (SELECT * FROM online_payment_trx
               UNION ALL
               SELECT * FROM offline_payment_trx)
WHERE amount>1000
 
````

You can fetch, join, make sql-predicates and sorting and all other actions for abstract entities.

##Grouping of related entities

Often we need to have associative map in field as collection of related entities.
Annotation **GroupBy** can  be used for such purposes.

Where:

* **value** - contains list of entity's fields that will be used as grouping keys. You can also can use fields of related entities - in this case  this related entities will be added to query as corresponding intersection.
* **type** - grouping type. **GroupByType.MAP** - grouping by unique field, result will be Map<?,?>; **GroupBy.MAP_OF_LIST** - grouping by non-unique field, result will be Map<?, List<?>>.

Example:

```java
@Entity(name="Agent", table="agent")
public class Agent{
  @IdField("id")
  private Long id;
 
  @OneToMany(relationProperty="agentId", relationClass=Contract.class)
  @GroupBy("code")
  private Map<String,Contract> contractAsMap; //group by Contract.code field
 
}
 
@Entity(name="Contract", table="contract")
public class Contract{
   @IdField("id")
   private Long id;
 
   @Field("agent_id")
   private Long agentId;
 
   @Field("code")
   private String code;
 
 
   @Field("status")
   private Integer status;
}
 
...
 
IEntityManager em = ...;
List<Agent> items = ...;
em.fetchLazy(Agent.class, items):
 
for(Agent a : items){
   Map<String,Cotract> contracts = a.getContractAsMap();// PROFIT! SELECT * FROM contract where agent_id IN(?)
}
````

##Additional conditions for relations

In some cases we need to have additional conditions for relation.

For example, in previous example we can divide agent relations by auxiliary condition: is contract active or not?
We can use **@Condition** annotation for this purposes:

Where:

* **value**  - SpEL expression in scope of class **If**; it describes additional condition for relation . Besides this predicate can contain condition by related entities which will be added to query as intersection.

Example:

```java
@Entity(name="Agent", table="agent")
public class Agent{
  @IdField("id")
  private Long id;
 
  @OneToMany(relationProperty="agentId", relationClass=Contract.class)
  @Condition("isEqual("status",valueOf(1))")
  private List<Contract> activeContracts;
 
  @OneToMany(relationProperty="agentId", relationClass=Contract.class)
  @Condition("isEqual("status",valueOf(2))") 
  @GroupBy("code")
  private Map<String,Contract> blockedContracts;
 
}
 
 
@Entity(name="Contract", table="contract")
public class Contract{
   @IdField("id")
   private Long id;
 
   @Field("agent_id")
   private Long agentId;
 
   @Field("code")
   private String code;
 
   @Field("status")
   private Integer status;
 
}
 
...
 
IEntityManager em = ...;
List<Agent> items = ...;
em.fetchLazy(Agent.class, items):
 
 
for(Agent a : items){
   Map<String,Cotract> contracts = a.getBlockedContracts();// PROFIT! SELECT * FROM contract where agent_id IN(?) AND status=2
   List<Cotract> contracts = a.getActiveContracts();// PROFIT! SELECT * FROM contract where agent_id IN(?) AND status=1
}
````

As you can see, we can mix **@Condition**  and **@Group**.

##Fetch ORM-entity inside non-ORM entity

Methods **IEntityManager.fetch**, **IEntityManager.fetchLazy** can be used not only for ORM-entity, but also for plain classes, that have fields with **@Association**, **@ManyToOne** annotaions.
May be this approach is seen controversially, because of it blurs DAO-level in our architecture. But it really simplify your code. Besides neither Hibernate or JPA don't hide  entirely their architectural details, and you have to keep them in mind. For example you can't read relation outside current session in Hibernate, so you have to options: use different domain objects in your business operations or blur DAO-level. I prefer the second, because the first choice it to cumbersome.

Example:

```java
@Entity()
public class AmountTrx{
   @ManyToOne ..... 
   private Card card
}
 
@Entity()
public class Card{
   ...
  @OneToMany ...
  @GroupBy("cardId,ticketTypeId")
  private Map<Long,Map<Long,TicketDetail> ticketDetails
}
 
public class ProcessPaymentEvent{
 
    private  String agentId;
    private String paymentID;
 
    @Association(property="agentInternalId", relationProperty="id")
    private Agent agent;
 
    @Association(property="trxInternalId", relationProperty="id")
    private AmountTrx trx;
 
    public String getAgentInternalId(){
      return new PCID(agentId).getInternalId();
    }
 
    public String getTrxInternalId(){
      return new PCID(agentId).getInternalId();
    }
}

....

   Collection<ProcessPaymentEvent> events = ...
   em.fetchLazy(events);
   
   for(ProcessPaymentEvent e: events){
      AmountTrx trx =  e.getAmountTrx() ;// SELECT * FROM AmountTrx WHERE ID IN(?)
      Card card = trx.getCard();// SELECT * FROM CARD WHERE ID IN(?)
   } 
````

You can see that this style is more declarative and helps to devote more time for business logic then DAO-level.

##Advanced locking of objects

New version of framework contains functionality for performing pessimistic locking of objects.
There are several types of advanced locking strategies:

* **LockType.WAIT** -pessimistic locking of object (SELECT FOR UPDATE). We will be waiting for lock until object is locked by another process.
* **LockType.NOWAIT** - pessimistic locking of object (SELECT FOR UPDATE). Locking will be failed if there is another process holding a lock for this object
* **LockType.SKIP_LOCKED** - pessimistic locking of object (SELECT FOR UPDATE).  Locking will be skipped in result if there is another process holding a lock for this object. This method is very useful to build quering.
* **LockType.INC_VERSION**  - optimistic locking  (UPDATE SET version=version+1)


Following method is used to acquire locks for objects:

```java
public interface IEntityManager{
    ... 
    <T> LockResult<T> lock(LockType type, Class<T> entityClass, Collection<T> items); 
    ... 
}
````

Result of this operation contains:

* **LockResult.getLocked()** - entities that were locked.
* **LockResult.getUnlocked()** - entities that weren't locked.

Different RDBMSes have different support of all this functionality.

RDBMS|LockType.WAIT|LockType.NOWAIT|LockType.SKIP_LOCKED|LockType.INC_VERSION
----|-------------|---------------|--------------------|--------------------
Oracle|	native|	native|	native|	native
MSSQL|	native(it can be lock escalation)|native(it can be lock escalation)|	native(it can be lock escalation)|	native
MySQL|	native|	Not efficient. It's implemented by setting locking timeout|Not efficient. It's implemented by setting locking timeout|	native
PosgreSQL|native|	native|	You have to use auxiliary contrib module: admin and Advisory Lock. You also have to keep in mind that Advisory Lock uses global _bigint_ indentifiers, so it works only for entities with  key of type *Long*. Gloal key is calculated as  hash of identifier and object type , so theoretically we can have collision here|	native

##Filtering API

Usually when you build web application you have to create data controller that return data filtered by column values. It is a challenge to build such queries, for example if you have to filter data by many conditions.
So as it's not convenient to use either IQuery interface nor jdbc queries interface for that we create special Filtering API.
Queries build with special  **Filter** that has following parameters: 

* **use** - is this filter used to build query or skipped. For example, here you can put bool expression to check incoming parameter value for NULL
* **condition** - it is classical predicate building with composition of **If**.
* **params** - list of parameters values in order how they are appeared in condition

```java
public class TestEntityController{
  @Resource(name = "dbtool.IEntityManager")
  private IEntityManager em;
 
 
 public List<TestEntity> getEntityPage(Interger pageCount, Integer pageSize, Integer pageOffset
                                       Date fromDate, Date toDate,
                                       String titleLike,
                                       Integer status,     
                                       ) {
    IFiltering<TestEntity> filtering = em.filtering(TestEntity.class);
    filtering
             .paging(pageCount*pageSize,pageSize)
             .filter(fromDate!=null && toDate!=null, If.between("date"), dateFrom, dateTo)
             .filter(titleLike!=null, If.like("title"),titleLike)
             .filter(status!=null, Condition.between("status"), status))
             .orderBy("id DESC");
    List<TestEntity> testEntities = filtering.selectList();
 
   return testEntities
}
````

**Remarks:**

* you can use joining and fetching of dependencies  as with **IQueryBuilder** interface
* you can use paging(offset,maxSize) as with **IQueryBuilder** interface


##How to use

You can attach framework as maven artifact or however you like.

**MavenConfig**

```xml

<dependency>
      <groupId>ru.kwanza.dbtool</groupId>
      <artifactId>dbtool-orm</artifactId>
      <version>${dbtool.version}</version>
 </dependency>
````

**Spring config:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beanshttp://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
 
    <import resource="classpath:dbtool-orm-config.xml"/>
    <import resource="classpath:dbtool-core-config.xml"/>
 
</beans>
````

After including this contexts you can inject beans:

* **dbtool.IEntityManager**- ru.kwanza.dbtool.orm.impl.EntityManagerImpl
* **dbtool.IEntityMappingRegistry** - ru.kwanza.dbtool.orm.impl.mapping.IEntityMappingRegistry, use it if you need direct access for entity registry information or for manual entity registration(not recommended)
* 
This context requires definition of bean

* **dataSource**  - javax.sql.DataSource

**Automatic entity mapping**

Just specify package we DBTool can find entities.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dbtool-orm="http://www.kwanza-systems.ru/schema/dbtool-orm"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.kwanza-systems.ru/schema/dbtool-orm http://www.kwanza-systems.ru/schema/dbtool-orm.xsd">
 
    <import resource="classpath:dbtool-config.xml"/>
 
    <dbtool-orm:entityMapping scan-package="ru.company.application.entity.core"/>
    <dbtool-orm:entityMapping scan-package="ru.company.application.entity.protocol"/>
    <dbtool-orm:entityMapping scan-package="ru.company.application.entity.payment;ru.company.application.entity.register"/>
 
</beans>
````   

#Benchmarking

To compare how efficient DBTool we choose Hibernate.

Our benchmarking has two scenarios:

* Compare select performance
* Compare update performance

We use [JMH](http://openjdk.java.net/projects/code-tools/jmh/) framework to implement our tests.
All benchmark sources are here: [research.hiber_vs_dbtool](https://bitbucket.org/aguzanov/research.hiber_vs_dbtool/src/)

**Select scenario:**
 
We have three entities: __StressEntity__, __StressEntityA__, __StressEntityB__. Each entity has 100 fields and 10000 rows in the table. Entity _StressEntity_ has one-to-one relations with entities __StressEntityA__, __StressEntityB__. In this scenario we select __StessEntity__ and fetch relations to __StressEntityA__, __StressEntityB__ with auxilary  queries. As a result we have three queries each of which returns 10000 entities.

**Update scenario:**

In this scenario we  batch update StressEntity which has 100 fields and 10000 rows in the table.

##Results
  
###Throughput

Benchmark|Mode|Samples|Score|Score error|Units
---------|----|-------|-----|-----------|-----
DBTool.select|avgt|100|1.833|0.129|s/op
DBTool.update|avgt|100|0.619|0.148|s/op
Hibernate.select|avgt|100|9.392|0.178|s/op
Hibernate.update|avgt|100|1.080|0.143|s/op

  
###Memory footprint

We use also __JMH__  with  __hs_gs__ profile
We analyze Eden Space **sun.gc.generation.0.space.0.used**  - how many new objects will be generated/released for each iteration.
 
**Select**

Iteration|	DBTOOL|	Hibernate
---------|------------|--------------
1|	203.803|	405.818
2|	202.492|	-1188.889
3|	202.491|	407.590
4|	202.499|	374.374
5|	-811.260|	405.588
6|	203.350|	-1199.408
7|	202.491|	351.598
8|	202.496|	399.396
9|	202.495|	399.419
10|	-798.683|	399.392


**Update**

Iteration|DBTOOL|Hibernate
---------|------|----------
1|121.515|393.724
2|81.383|397.046
3|-383.561|387.283
4|40.539|-1560.520
5|162.156|403.558
6|40.539|403.558
7|162.156|396.434
8|-364.008|286.258
9|82.769|-1574.644
10|160.616|439.153


###Summary 
* DBTool is about **5 times faster** than Hibernate in the select scenario 
* DBTool is about **2 times faster** than Hibernate in the update scenario 
* DBTool is about **2 times less memory-consuming**  than hibernate in the update scenario: DBTool generates about 200Мб of new objects in average, but Hibernate is 400 Mb
* DBTool is about **4 times less memory-consuming**  than hibernate in the update scenario: DBTool generates about 100МБ of new objects in average, but Hibernate is 400 Mb
