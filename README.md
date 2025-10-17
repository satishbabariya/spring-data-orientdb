# Spring Data OrientDB

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OrientDB](https://img.shields.io/badge/OrientDB-3.2.32-blue.svg)](https://orientdb.org)
[![GitHub](https://img.shields.io/badge/GitHub-satishbabariya-blue.svg)](https://github.com/satishbabariya/spring-data-orientdb)

Spring Data OrientDB provides a familiar and consistent Spring Data interface for [OrientDB](https://orientdb.org), the powerful multi-model graph database. This library brings the full power of Spring Data's repository abstraction, query derivation, transaction management, and entity lifecycle callbacks to OrientDB's graph and document capabilities.

> **Note**: This is a community-driven project and is not officially supported by the Spring team. It provides Spring Data integration for OrientDB based on Spring Data Commons patterns.

## 🚀 Features

### Core Functionality
- **🔄 Spring Data Repository Support**: Full implementation of Spring Data's repository abstraction with `CrudRepository`, `PagingAndSortingRepository`
- **📝 Annotation-Based Mapping**: Declarative entity mapping with `@Vertex`, `@Edge`, `@Property`, `@Id`, `@Version`
- **🎯 Automatic Repository Implementation**: Define repository interfaces, get thread-safe implementations automatically
- **🔍 Query Method Derivation**: Auto-generate queries from method names (e.g., `findByUsernameAndEmail`)
- **💾 Template Support**: Flexible `OrientDBTemplate` for custom operations and complex queries
- **🔐 Transaction Management**: Full integration with Spring's `@Transactional` and transaction synchronization
- **📊 Pagination & Sorting**: Native support for `Pageable` and `Sort` with efficient OrientDB queries

### Advanced Features
- **📈 Query By Example (QBE)**: Dynamic query generation using `Example<T>` API
- **🔎 Custom @Query Support**: Write custom OrientDB SQL queries with named parameters
- **📁 Named Queries**: Define reusable queries in `orientdb-named-queries.properties`
- **👥 Auditing Support**: Automatic timestamp and user tracking with `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`
- **🎭 Entity Lifecycle Callbacks**: `@PrePersist`, `@PostLoad`, `@PreRemove` event hooks
- **🏗️ Schema Generation**: Automatic OrientDB vertex/edge class creation from entity definitions
- **🔄 Type Conversion**: Bidirectional conversion between Java objects and OrientDB documents
- **🎯 Projection Support**: Interface-based and class-based DTOs for selective data retrieval
- **📊 Observability & Metrics**: Integration with Micrometer for operation monitoring
- **⚡ Async Repository Support**: Non-blocking operations with `AsyncOrientDBRepository`
- **🎨 Caching Integration**: Built-in support for Spring Cache abstraction

## 📋 Requirements

- **Java**: 17 or higher
- **Spring Boot**: 3.3.5+
- **Spring Data Commons**: 3.3.5+
- **OrientDB**: 3.2.32+
- **Maven**: 3.6+ or Gradle 7+

## 📦 Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-orientdb</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- OrientDB dependencies (if not already included) -->
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-core</artifactId>
    <version>3.2.32</version>
</dependency>
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-client</artifactId>
    <version>3.2.32</version>
</dependency>
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>orientdb-graphdb</artifactId>
    <version>3.2.32</version>
</dependency>
```

### Gradle

```gradle
implementation 'org.springframework.data:spring-data-orientdb:0.0.1-SNAPSHOT'
implementation 'com.orientechnologies:orientdb-core:3.2.32'
implementation 'com.orientechnologies:orientdb-client:3.2.32'
implementation 'com.orientechnologies:orientdb-graphdb:3.2.32'
```

## 🔧 Configuration

### Basic Configuration

Create a configuration class that extends `AbstractOrientDBConfiguration`:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orientdb.config.AbstractOrientDBConfiguration;
import org.springframework.data.orientdb.repository.config.EnableOrientDBRepositories;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;

@Configuration
@EnableOrientDBRepositories(basePackages = "com.example.repository")
public class OrientDBConfiguration extends AbstractOrientDBConfiguration {
    
    @Override
    protected OrientDB orientDB() {
        // Embedded mode
        return new OrientDB("embedded:./databases", OrientDBConfig.defaultConfig());
        
        // Or remote mode
        // return new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
    }
    
    @Override
    protected String getDatabaseName() {
        return "myapp";
    }
    
    @Override
    protected String getUsername() {
        return "admin";
    }
    
    @Override
    protected String getPassword() {
        return "admin";
    }
}
```

### Advanced Configuration with All Features

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orientdb.config.AbstractOrientDBConfiguration;
import org.springframework.data.orientdb.config.EnableOrientDBAuditing;
import org.springframework.data.orientdb.config.EnableOrientDBCaching;
import org.springframework.data.orientdb.config.EnableOrientDBObservability;
import org.springframework.data.orientdb.config.EnableOrientDBTransactionManagement;
import org.springframework.data.orientdb.repository.config.EnableOrientDBRepositories;

@Configuration
@EnableOrientDBRepositories(basePackages = "com.example.repository")
@EnableOrientDBAuditing(auditorAwareRef = "auditorProvider")
@EnableOrientDBCaching
@EnableOrientDBObservability
@EnableOrientDBTransactionManagement
public class OrientDBConfiguration extends AbstractOrientDBConfiguration {
    
    @Override
    protected OrientDB orientDB() {
        OrientDBConfig config = OrientDBConfig.builder()
            .addConfig(OGlobalConfiguration.DB_POOL_MIN, 5)
            .addConfig(OGlobalConfiguration.DB_POOL_MAX, 20)
            .build();
        return new OrientDB("remote:localhost", config);
    }
    
    @Override
    protected String getDatabaseName() {
        return "production_db";
    }
    
    @Override
    protected String getUsername() {
        return System.getenv("ORIENTDB_USER");
    }
    
    @Override
    protected String getPassword() {
        return System.getenv("ORIENTDB_PASSWORD");
    }
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityContextHolder.getContext()
            .getAuthentication().getName());
    }
}
```

### application.properties

```properties
# OrientDB Configuration
orientdb.url=remote:localhost
orientdb.database=myapp
orientdb.username=admin
orientdb.password=admin

# Connection Pool
orientdb.pool.min=5
orientdb.pool.max=20

# Schema Generation
orientdb.schema.auto-generate=true

# Logging
logging.level.org.springframework.data.orientdb=DEBUG
```

## 🎯 Quick Start Guide

### 1. Define Your Entities

```java
import org.springframework.data.orientdb.core.schema.*;
import com.orientechnologies.orient.core.id.ORID;

@Vertex("Person")
public class Person {
    
    @Id
    private ORID id;
    
    private String firstName;
    
    private String lastName;
    
    @Property("email_address")
    private String email;
    
    private Integer age;
    
    private String department;
    
    private Boolean active;
    
    @Edge(type = "KNOWS", direction = Edge.Direction.OUTGOING)
    private List<Person> friends;
    
    @Edge(type = "WORKS_FOR", direction = Edge.Direction.OUTGOING)
    private Company company;
    
    @Version
    private Integer version;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
    
    // Lifecycle callbacks
    @PrePersist
    public void prePersist() {
        if (active == null) {
            active = true;
        }
    }
    
    @PostLoad
    public void postLoad() {
        // Initialize transient fields, etc.
    }
    
    @PreRemove
    public void preRemove() {
        // Cleanup before deletion
    }
    
    // Constructors, getters, setters, toString, equals, hashCode...
}

@Vertex("Company")
public class Company {
    
    @Id
    private ORID id;
    
    private String name;
    
    private String industry;
    
    @Edge(type = "WORKS_FOR", direction = Edge.Direction.INCOMING)
    private List<Person> employees;
    
    // Constructors, getters, setters...
}
```

### 2. Create Repository Interfaces

```java
import org.springframework.data.orientdb.repository.OrientDBRepository;
import org.springframework.data.orientdb.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.orientechnologies.orient.core.id.ORID;

public interface PersonRepository extends OrientDBRepository<Person, ORID> {
    
    // ========== Derived Query Methods (Auto-generated) ==========
    
    Person findByEmail(String email);
    
    List<Person> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<Person> findByAgeGreaterThan(Integer age);
    
    List<Person> findByAgeGreaterThanEqual(Integer age);
    
    List<Person> findByAgeLessThan(Integer age);
    
    List<Person> findByDepartmentAndActive(String department, Boolean active);
    
    List<Person> findByFirstNameContaining(String name);
    
    List<Person> findByFirstNameStartingWith(String prefix);
    
    List<Person> findByFirstNameEndingWith(String suffix);
    
    List<Person> findByAgeIn(Collection<Integer> ages);
    
    List<Person> findByActiveTrue();
    
    List<Person> findByActiveFalse();
    
    List<Person> findByEmailIsNull();
    
    List<Person> findByEmailIsNotNull();
    
    // Sorting
    List<Person> findByDepartmentOrderByLastNameAsc(String department);
    
    // Limiting
    List<Person> findTop10ByOrderByCreatedAtDesc();
    
    Person findFirstByOrderByCreatedAtAsc();
    
    // Count and Exists
    long countByDepartment(String department);
    
    boolean existsByEmail(String email);
    
    // Delete operations
    long deleteByInactiveSince(LocalDateTime date);
    
    void removeByActive(Boolean active);
    
    // ========== Custom Queries ==========
    
    @Query("SELECT FROM Person WHERE firstName LIKE :pattern OR lastName LIKE :pattern")
    List<Person> searchByName(@Param("pattern") String pattern);
    
    @Query("SELECT expand(out('KNOWS')) FROM Person WHERE @rid = :personId")
    List<Person> findFriends(@Param("personId") ORID personId);
    
    @Query("SELECT FROM Person WHERE department = :dept AND age > :minAge")
    Page<Person> findByDepartmentAndAgeGreaterThan(
        @Param("dept") String department, 
        @Param("minAge") Integer minAge, 
        Pageable pageable
    );
    
    @Query("MATCH {class: Person, as: p}-WORKS_FOR->{class: Company, as: c, where: (name = :companyName)} RETURN p")
    List<Person> findEmployeesByCompany(@Param("companyName") String companyName);
    
    // ========== Pagination & Sorting ==========
    
    Page<Person> findByDepartment(String department, Pageable pageable);
    
    Slice<Person> findByActive(Boolean active, Pageable pageable);
    
    // ========== Query By Example ==========
    
    // Inherited from OrientDBRepository:
    // <S extends Person> Optional<S> findOne(Example<S> example);
    // <S extends Person> Iterable<S> findAll(Example<S> example);
    // <S extends Person> Page<S> findAll(Example<S> example, Pageable pageable);
}
```

### 3. Use Repositories in Services

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

@Service
@Transactional
public class PersonService {
    
    private final PersonRepository personRepository;
    
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    
    // Create
    public Person createPerson(String firstName, String lastName, String email) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setActive(true);
        return personRepository.save(person);
    }
    
    // Read
    public Optional<Person> findById(ORID id) {
        return personRepository.findById(id);
    }
    
    public Person findByEmail(String email) {
        return personRepository.findByEmail(email);
    }
    
    // Update
    public Person updatePerson(ORID id, String newEmail) {
        Person person = personRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        person.setEmail(newEmail);
        return personRepository.save(person);
    }
    
    // Delete
    public void deletePerson(ORID id) {
        personRepository.deleteById(id);
    }
    
    // Pagination
    public Page<Person> getAllPersons(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by("lastName").ascending().and(Sort.by("firstName")));
        return personRepository.findAll(pageable);
    }
    
    // Query By Example
    public List<Person> findSimilarPersons(Person probe) {
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnorePaths("id", "version")
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase();
        Example<Person> example = Example.of(probe, matcher);
        return personRepository.findAll(example);
    }
    
    // Custom query
    public List<Person> searchPersons(String searchTerm) {
        return personRepository.searchByName("%" + searchTerm + "%");
    }
    
    // Graph traversal
    public List<Person> getFriends(ORID personId) {
        return personRepository.findFriends(personId);
    }
    
    // Batch operations
    public List<Person> createMultiplePersons(List<Person> persons) {
        return personRepository.saveAll(persons);
    }
    
    // Count
    public long countByDepartment(String department) {
        return personRepository.countByDepartment(department);
    }
    
    // Exists
    public boolean emailExists(String email) {
        return personRepository.existsByEmail(email);
    }
}
```

### 4. Using OrientDBTemplate for Advanced Operations

```java
import org.springframework.data.orientdb.core.OrientDBTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdvancedPersonService {
    
    private final OrientDBTemplate orientDBTemplate;
    
    public AdvancedPersonService(OrientDBTemplate orientDBTemplate) {
        this.orientDBTemplate = orientDBTemplate;
    }
    
    // Execute custom query
    public List<Person> findActivePersonsInDepartment(String department) {
        String query = "SELECT FROM Person WHERE department = ? AND active = true ORDER BY lastName";
        return orientDBTemplate.query(query, Person.class, department);
    }
    
    // Single result query
    public Optional<Person> findPersonByEmailTemplate(String email) {
        String query = "SELECT FROM Person WHERE email = ?";
        return orientDBTemplate.querySingle(query, Person.class, email);
    }
    
    // Execute command
    public void createEdgeBetweenPersons(ORID personId1, ORID personId2) {
        orientDBTemplate.execute(session -> {
            String command = "CREATE EDGE KNOWS FROM ? TO ?";
            session.command(command, personId1, personId2);
            return null;
        });
    }
    
    // Complex graph query
    public List<Person> findFriendsOfFriends(ORID personId) {
        String query = "SELECT expand(out('KNOWS').out('KNOWS')) FROM Person WHERE @rid = ?";
        return orientDBTemplate.query(query, Person.class, personId);
    }
    
    // Bulk insert
    public void bulkInsert(List<Person> persons) {
        orientDBTemplate.execute(session -> {
            persons.forEach(person -> {
                OVertex vertex = session.newVertex("Person");
                vertex.setProperty("firstName", person.getFirstName());
                vertex.setProperty("lastName", person.getLastName());
                vertex.setProperty("email", person.getEmail());
                vertex.save();
            });
            session.commit();
            return null;
        });
    }
    
    // Native OrientDB session access
    public void performComplexOperation() {
        orientDBTemplate.execute(session -> {
            // Full access to OrientDB session
            OResultSet results = session.query("MATCH ...");
            // Process results...
            session.commit();
            return null;
        });
    }
}
```

## 📚 Annotation Reference

### Entity Annotations

#### `@Vertex`
Marks a class as an OrientDB vertex entity.

```java
@Vertex("User")           // Maps to OrientDB class "User"
@Vertex                   // Uses class name as vertex class
public class User { }
```

#### `@Edge`
Defines graph relationships between vertices.

```java
@Edge(type = "KNOWS", direction = Direction.OUTGOING)
private List<Person> friends;

@Edge(type = "WORKS_FOR", direction = Direction.INCOMING)
private Company company;

@Edge(type = "MANAGES", direction = Direction.BOTH)
private List<Person> managementRelationships;
```

**Direction Options:**
- `Direction.OUTGOING`: Outgoing edges from this vertex
- `Direction.INCOMING`: Incoming edges to this vertex
- `Direction.BOTH`: Bidirectional edges

#### `@Id`
Marks the identifier field (typically `ORID`).

```java
@Id
private ORID id;
```

#### `@Property`
Customizes property name mapping.

```java
@Property("user_name")
private String username;

@Property("email_address")
private String email;
```

#### `@Version`
Enables optimistic locking with version checking.

```java
@Version
private Integer version;
```

### Auditing Annotations

Enable with `@EnableOrientDBAuditing` on your configuration class.

```java
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime updatedAt;

@CreatedBy
private String createdBy;

@LastModifiedBy
private String lastModifiedBy;
```

### Lifecycle Callback Annotations

```java
@PrePersist
public void beforeSave() {
    // Called before entity is saved
    this.slug = generateSlug(this.title);
}

@PostLoad
public void afterLoad() {
    // Called after entity is loaded from database
    this.initialized = true;
}

@PreRemove
public void beforeDelete() {
    // Called before entity is deleted
    cleanupResources();
}
```

### Query Annotation

```java
@Query("SELECT FROM Person WHERE age > :minAge AND department = :dept")
List<Person> findByAgeAndDepartment(
    @Param("minAge") Integer age, 
    @Param("dept") String department
);
```

## 🔍 Query Method Naming Convention

Spring Data OrientDB supports automatic query derivation from method names following Spring Data conventions:

### Supported Keywords

| Keyword | Sample | OrientDB SQL |
|---------|--------|-------------|
| `findBy` | `findByLastName(String name)` | `WHERE lastName = ?` |
| `And` | `findByFirstNameAndLastName(...)` | `WHERE firstName = ? AND lastName = ?` |
| `Or` | `findByFirstNameOrLastName(...)` | `WHERE firstName = ? OR lastName = ?` |
| `GreaterThan` | `findByAgeGreaterThan(Integer age)` | `WHERE age > ?` |
| `LessThan` | `findByAgeLessThan(Integer age)` | `WHERE age < ?` |
| `GreaterThanEqual` | `findByAgeGreaterThanEqual(...)` | `WHERE age >= ?` |
| `LessThanEqual` | `findByAgeLessThanEqual(...)` | `WHERE age <= ?` |
| `Between` | `findByAgeBetween(Integer from, Integer to)` | `WHERE age BETWEEN ? AND ?` |
| `Like` | `findByFirstNameLike(String pattern)` | `WHERE firstName LIKE ?` |
| `NotLike` | `findByFirstNameNotLike(...)` | `WHERE firstName NOT LIKE ?` |
| `StartingWith` | `findByFirstNameStartingWith(...)` | `WHERE firstName LIKE '?%'` |
| `EndingWith` | `findByFirstNameEndingWith(...)` | `WHERE firstName LIKE '%?'` |
| `Containing` | `findByFirstNameContaining(...)` | `WHERE firstName LIKE '%?%'` |
| `In` | `findByAgeIn(Collection<Integer> ages)` | `WHERE age IN ?` |
| `NotIn` | `findByAgeNotIn(...)` | `WHERE age NOT IN ?` |
| `True` | `findByActiveTrue()` | `WHERE active = true` |
| `False` | `findByActiveFalse()` | `WHERE active = false` |
| `IsNull` | `findByEmailIsNull()` | `WHERE email IS NULL` |
| `IsNotNull` | `findByEmailIsNotNull()` | `WHERE email IS NOT NULL` |
| `OrderBy` | `findByDepartmentOrderByLastNameAsc(...)` | `ORDER BY lastName ASC` |
| `Top/First` | `findTop10ByOrderByAgeDesc()` | `LIMIT 10` |
| `Distinct` | `findDistinctByLastName(...)` | `SELECT DISTINCT` |
| `countBy` | `countByDepartment(String dept)` | `SELECT count(*) WHERE ...` |
| `existsBy` | `existsByEmail(String email)` | Returns boolean |
| `deleteBy` | `deleteByActive(Boolean active)` | `DELETE WHERE ...` |
| `removeBy` | `removeByDepartment(String dept)` | `DELETE WHERE ...` |

## 🔐 Transaction Management

Spring Data OrientDB fully integrates with Spring's transaction management:

```java
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;

@Service
@Transactional
public class TransactionalService {
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    // Method-level transaction
    @Transactional
    public void createPersonWithCompany(Person person, Company company) {
        companyRepository.save(company);
        person.setCompany(company);
        personRepository.save(person);
        // Both operations committed together
    }
    
    // Read-only transaction (optimization)
    @Transactional(readOnly = true)
    public Person findPerson(ORID id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException());
    }
    
    // Propagation control
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void independentTransaction() {
        // Runs in new transaction, independent of caller
    }
    
    // Rollback configuration
    @Transactional(rollbackFor = Exception.class)
    public void methodThatMayFail() {
        // Rolls back on any exception
    }
    
    // Timeout configuration
    @Transactional(timeout = 30)
    public void longRunningOperation() {
        // Transaction times out after 30 seconds
    }
}
```

## 📊 Pagination and Sorting

### Basic Pagination

```java
import org.springframework.data.domain.*;

// Simple pagination
Pageable pageable = PageRequest.of(0, 20); // Page 0, size 20
Page<Person> page = personRepository.findAll(pageable);

System.out.println("Total elements: " + page.getTotalElements());
System.out.println("Total pages: " + page.getTotalPages());
System.out.println("Current page: " + page.getNumber());
System.out.println("Page size: " + page.getSize());
System.out.println("Has next: " + page.hasNext());

// Iterate results
page.getContent().forEach(person -> {
    System.out.println(person.getFirstName());
});
```

### Pagination with Sorting

```java
// Single field sort
Sort sort = Sort.by("lastName").ascending();
Pageable pageable = PageRequest.of(0, 20, sort);

// Multiple field sort
Sort sort = Sort.by("lastName").ascending()
                .and(Sort.by("firstName").ascending());
Pageable pageable = PageRequest.of(0, 20, sort);

// Sort with direction
Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

// Complex sorting
Sort sort = Sort.by("department").ascending()
                .and(Sort.by("age").descending())
                .and(Sort.by("lastName").ascending());
```

### Dynamic Sorting

```java
public Page<Person> searchWithDynamicSort(
    String department, 
    int page, 
    int size,
    String sortBy, 
    String direction
) {
    Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") 
        ? Sort.Direction.ASC 
        : Sort.Direction.DESC;
    
    Pageable pageable = PageRequest.of(
        page, 
        size, 
        Sort.by(sortDirection, sortBy)
    );
    
    return personRepository.findByDepartment(department, pageable);
}
```

## 🔎 Query By Example (QBE)

Query By Example provides a simple way to create dynamic queries:

```java
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

// Simple example
Person probe = new Person();
probe.setDepartment("Engineering");
probe.setActive(true);

Example<Person> example = Example.of(probe);
List<Person> results = personRepository.findAll(example);

// Custom matcher
Person probe = new Person();
probe.setFirstName("john");
probe.setDepartment("Eng");

ExampleMatcher matcher = ExampleMatcher.matching()
    .withIgnorePaths("id", "version", "createdAt")
    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
    .withIgnoreCase()
    .withIgnoreNullValues();

Example<Person> example = Example.of(probe, matcher);
List<Person> results = personRepository.findAll(example);

// Field-specific matching
ExampleMatcher matcher = ExampleMatcher.matching()
    .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
    .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact())
    .withIgnorePaths("version");
```

## 🎭 Projection Support

### Interface-Based Projections

```java
// Closed projection (only specified properties)
public interface PersonSummary {
    String getFirstName();
    String getLastName();
    String getEmail();
    
    // Computed property
    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}

// Usage
public interface PersonRepository extends OrientDBRepository<Person, ORID> {
    List<PersonSummary> findByDepartment(String department);
    
    <T> List<T> findByActive(Boolean active, Class<T> type);
}

// In service
List<PersonSummary> summaries = personRepository.findByDepartment("Engineering");
summaries.forEach(s -> System.out.println(s.getFullName()));

// Dynamic projection
List<PersonSummary> summaries = personRepository.findByActive(true, PersonSummary.class);
```

### Class-Based Projections (DTOs)

```java
public class PersonDTO {
    private String firstName;
    private String lastName;
    private String email;
    
    public PersonDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters, setters...
}

// Repository method
public interface PersonRepository extends OrientDBRepository<Person, ORID> {
    List<PersonDTO> findByDepartment(String department);
}
```

## 📈 Observability and Metrics

Enable observability to track repository operations:

```java
@Configuration
@EnableOrientDBObservability
public class ObservabilityConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
```

**Metrics collected:**
- `orientdb.repository.invocations`: Count of repository method calls
- `orientdb.repository.execution.time`: Execution time of repository methods
- `orientdb.query.execution.time`: Query execution times
- `orientdb.connection.active`: Active database connections
- `orientdb.transaction.commit`: Successful transaction commits
- `orientdb.transaction.rollback`: Transaction rollbacks

## 🔄 Async Repository Support

For non-blocking operations:

```java
import org.springframework.data.orientdb.repository.AsyncOrientDBRepository;
import java.util.concurrent.CompletableFuture;

public interface PersonAsyncRepository extends AsyncOrientDBRepository<Person, ORID> {
    
    CompletableFuture<Person> findByEmail(String email);
    
    CompletableFuture<List<Person>> findByDepartment(String department);
    
    CompletableFuture<Person> save(Person person);
}

// Usage
@Service
public class AsyncPersonService {
    
    @Autowired
    private PersonAsyncRepository repository;
    
    public CompletableFuture<Person> createPersonAsync(Person person) {
        return repository.save(person)
            .thenApply(saved -> {
                // Process saved person
                return saved;
            });
    }
    
    public CompletableFuture<List<Person>> searchMultipleDepartments() {
        CompletableFuture<List<Person>> eng = 
            repository.findByDepartment("Engineering");
        CompletableFuture<List<Person>> sales = 
            repository.findByDepartment("Sales");
        
        return eng.thenCombine(sales, (engineers, salespeople) -> {
            List<Person> combined = new ArrayList<>(engineers);
            combined.addAll(salespeople);
            return combined;
        });
    }
}
```

## 🏗️ Schema Generation

Automatic schema generation from entity classes:

```java
@Configuration
@EnableOrientDBRepositories
public class OrientDBConfig extends AbstractOrientDBConfiguration {
    
    // ... connection configuration ...
    
    @Bean
    public SchemaGenerator schemaGenerator() {
        return new SchemaGenerator();
    }
    
    @Bean
    @DependsOn("schemaGenerator")
    public SchemaGeneratorInitializer schemaInitializer(
        SchemaGenerator generator,
        OrientDBMappingContext context
    ) {
        return new SchemaGeneratorInitializer(generator, context);
    }
}
```

The schema generator will:
1. Create vertex classes for `@Vertex` annotated entities
2. Create properties based on entity fields
3. Create indexes for `@Id` and `@Version` fields
4. Set up relationships for `@Edge` annotations
5. Configure constraints and data types

## 🧪 Testing

### Integration Test Example

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersonRepositoryIT {
    
    @Autowired
    private PersonRepository personRepository;
    
    @Test
    public void testCreateAndFindPerson() {
        // Create
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("john.doe@example.com");
        
        Person saved = personRepository.save(person);
        assertNotNull(saved.getId());
        
        // Find
        Person found = personRepository.findByEmail("john.doe@example.com");
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }
    
    @Test
    public void testPagination() {
        // Create test data
        for (int i = 0; i < 50; i++) {
            Person person = new Person();
            person.setFirstName("Person" + i);
            person.setLastName("Test");
            personRepository.save(person);
        }
        
        // Test pagination
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> page = personRepository.findAll(pageable);
        
        assertEquals(10, page.getContent().size());
        assertEquals(50, page.getTotalElements());
        assertEquals(5, page.getTotalPages());
    }
    
    @Test
    @Transactional
    public void testTransactionalRollback() {
        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Rollback");
        
        personRepository.save(person);
        
        // Force exception to trigger rollback
        throw new RuntimeException("Rollback test");
        
        // Person should not be saved due to rollback
    }
}
```

### Unit Test with Mocking

```java
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    
    @Mock
    private PersonRepository personRepository;
    
    @InjectMocks
    private PersonService personService;
    
    @Test
    public void testCreatePerson() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        
        when(personRepository.save(any(Person.class)))
            .thenReturn(person);
        
        Person created = personService.createPerson("John", "Doe", "john@example.com");
        
        assertNotNull(created);
        verify(personRepository, times(1)).save(any(Person.class));
    }
}
```

## 🎯 Best Practices

### 1. Entity Design
- Always use `@Id` with `ORID` type
- Use `@Version` for entities that may be updated concurrently
- Keep entities lightweight; use projections for read-heavy operations
- Use lifecycle callbacks for computed values and validation

### 2. Repository Design
- Prefer derived queries over `@Query` when possible
- Use `@Query` for complex graph traversals and joins
- Create custom repository fragments for complex logic
- Use projections to limit data transfer

### 3. Transaction Management
- Use `@Transactional` at service layer, not repository
- Mark read-only operations with `readOnly = true`
- Configure appropriate timeout values for long operations
- Handle transaction exceptions properly

### 4. Performance Optimization
- Use pagination for large result sets
- Enable caching for frequently accessed data
- Use indexes on frequently queried fields
- Consider async repositories for parallel operations
- Use projections instead of loading full entities

### 5. Connection Management
- Configure appropriate pool sizes
- Monitor active connections with metrics
- Close sessions properly (handled automatically by framework)
- Use connection pooling in production

### 6. Error Handling

```java
@Service
public class RobustPersonService {
    
    @Autowired
    private PersonRepository personRepository;
    
    public Optional<Person> findPersonSafely(ORID id) {
        try {
            return personRepository.findById(id);
        } catch (ODatabaseException e) {
            log.error("Database error finding person: " + id, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error finding person: " + id, e);
            throw new ServiceException("Failed to find person", e);
        }
    }
    
    @Transactional
    public Person updatePersonWithRetry(ORID id, String newEmail) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                Person person = personRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException());
                person.setEmail(newEmail);
                return personRepository.save(person);
            } catch (OConcurrentModificationException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new ServiceException("Max retries exceeded", e);
                }
                // Wait before retry
                Thread.sleep(100 * retryCount);
            }
        }
        throw new ServiceException("Update failed");
    }
}
```

## 🔧 Troubleshooting

### Common Issues

**Issue: "No OrientDB session found"**
```
Solution: Ensure @Transactional is present or use OrientDBTemplate
```

**Issue: "Optimistic locking failed"**
```
Solution: Add retry logic or refresh entity before update
```

**Issue: "Connection pool exhausted"**
```
Solution: Increase pool size or check for connection leaks
```

**Issue: "Query method not working"**
```
Solution: Verify method naming follows conventions
Check entity field names match method parameters
```

### Enable Debug Logging

```properties
logging.level.org.springframework.data.orientdb=DEBUG
logging.level.com.orientechnologies.orient=DEBUG
```

## 📊 Architecture Overview

```
┌─────────────────────────────────────────┐
│         Application Layer               │
│    (Services, Controllers, etc.)        │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      Spring Data OrientDB Layer         │
├─────────────────────────────────────────┤
│  - Repository Interfaces                │
│  - Query Derivation                     │
│  - Transaction Management               │
│  - Entity Callbacks                     │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Mapping & Conversion            │
├─────────────────────────────────────────┤
│  - OrientDBMappingContext               │
│  - OrientDBEntityConverter              │
│  - Type Conversion                      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         OrientDB Template               │
├─────────────────────────────────────────┤
│  - Query Execution                      │
│  - Session Management                   │
│  - Error Translation                    │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         OrientDB Client                 │
│    (orientdb-core, orientdb-client)     │
└─────────────────────────────────────────┘
```

## 📝 Example Application

Complete working example: [spring-data-orientdb-examples](https://github.com/satishbabariya/spring-data-orientdb-examples)

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request

### Development Setup

```bash
# Clone repository
git clone https://github.com/satishbabariya/spring-data-orientdb.git
cd spring-data-orientdb

# Build project
mvn clean install

# Run tests
mvn test

# Run integration tests
mvn verify
```

### Code Style
- Follow Spring Framework coding conventions
- Write comprehensive Javadoc for public APIs
- Include unit tests for new features
- Maintain test coverage above 70%

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Data Team**: For the excellent Spring Data Commons framework
- **OrientDB Team**: For the powerful multi-model database
- **Spring Data Neo4j**: Inspiration for the repository abstraction design
- **Community Contributors**: Thank you for your contributions and feedback

## 📞 Support and Contact

- **Issues**: [GitHub Issues](https://github.com/satishbabariya/spring-data-orientdb/issues)
- **Discussions**: [GitHub Discussions](https://github.com/satishbabariya/spring-data-orientdb/discussions)
- **Stack Overflow**: Tag questions with `spring-data-orientdb`
- **Author**: [@satishbabariya](https://github.com/satishbabariya)

## 🗺️ Roadmap

### Version 1.0.0 (Current)
- ✅ Core CRUD operations
- ✅ Repository abstraction
- ✅ Query method derivation
- ✅ Pagination and sorting
- ✅ Transaction management
- ✅ Entity lifecycle callbacks
- ✅ Auditing support
- ✅ Schema generation
- ✅ Projections
- ✅ Query By Example
- ✅ Observability & metrics

### Version 1.1.0 (Planned)
- ⏳ Spring Boot Starter auto-configuration
- ⏳ Reactive repository support (ReactiveOrientDBRepository)
- ⏳ Criteria API / Specifications
- ⏳ Lazy loading strategies
- ⏳ Enhanced caching strategies
- ⏳ Query hints and optimization

### Version 2.0.0 (Future)
- ⏳ Full reactive support with Project Reactor
- ⏳ GraphQL integration
- ⏳ Native GraalVM support
- ⏳ Advanced graph analytics
- ⏳ Multi-tenancy support

---

**Made with ❤️ for the Spring and OrientDB communities**
