# Spring Data OrientDB

Spring Data OrientDB provides a familiar Spring Data interface for [OrientDB](https://orientdb.org), the multi-model graph database. This library brings the power of Spring Data to OrientDB, enabling developers to leverage Spring's powerful repository abstraction and data access patterns with OrientDB's graph capabilities.

## Features

- **Spring Data Repository Support**: Leverage familiar Spring Data repository patterns
- **Annotation-Based Mapping**: Map your domain entities to OrientDB vertices and edges using annotations
- **Automatic Repository Implementation**: Define repository interfaces and get implementations automatically
- **Query Methods**: Derive queries from method names or use custom SQL queries
- **Template Support**: Use `OrientDBTemplate` for more flexible data access operations
- **Transaction Support**: Integrate with Spring's transaction management
- **Connection Pooling**: Built-in support for OrientDB connection pooling
- **Pagination**: Full support for Spring Data `Pageable` with sorting
- **Sorting**: Multi-field sorting with `Sort`
- **Query By Example**: Dynamic queries with `Example` API
- **Query Method Derivation**: Automatic query generation from method names!
- **Named Queries**: Define reusable queries in properties files
- **Auditing Support**: Automatic timestamp and user tracking!
- **Event Callbacks**: @PrePersist, @PostLoad, @PreRemove lifecycle hooks! ✨ NEW!
- **Schema Generation**: Automatic schema creation from entities! ✨ NEW!
- **Projections**: Interface and class-based DTOs! ✨ NEW!

## Architecture

This library is modeled after Spring Data Neo4j and provides:

- **Annotations**: `@Vertex`, `@Edge`, `@Property`, `@Id`, `@Version`
- **Repository Interfaces**: `OrientDBRepository<T, ID>`
- **Template Classes**: `OrientDBTemplate`, `OrientDBOperations`
- **Configuration**: `@EnableOrientDBRepositories`, `AbstractOrientDBConfiguration`
- **Mapping Context**: Handles entity metadata and property mapping

## Getting Started

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-orientdb</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Configuration

Create a configuration class extending `AbstractOrientDBConfiguration`:

```java
@Configuration
@EnableOrientDBRepositories(basePackages = "com.example.repository")
public class OrientDBConfig extends AbstractOrientDBConfiguration {
    
    @Override
    protected OrientDB orientDB() {
        // Embedded mode
        return new OrientDB("embedded:./databases", OrientDBConfig.defaultConfig());
        
        // Or remote mode
        // return new OrientDB("remote:localhost:2424", "root", "password", OrientDBConfig.defaultConfig());
    }
    
    @Override
    protected String getDatabaseName() {
        return "mydb";
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

### Entity Mapping

Define your domain entities using annotations:

```java
@Vertex("User")
public class User {
    
    @Id
    private ORID id;
    
    private String username;
    
    private String email;
    
    @Property("first_name")
    private String firstName;
    
    private String lastName;
    
    @Edge(type = "KNOWS", direction = Direction.OUTGOING)
    private List<User> friends;
    
    @Edge(type = "MEMBER_OF", direction = Direction.OUTGOING)
    private List<Group> groups;
    
    @Version
    private Integer version;
    
    // Constructors, getters, setters...
}

@Vertex("Group")
public class Group {
    
    @Id
    private ORID id;
    
    private String name;
    
    private String description;
    
    // Constructors, getters, setters...
}
```

### Repository Interface

Create repository interfaces extending `OrientDBRepository`:

```java
public interface UserRepository extends OrientDBRepository<User, ORID> {
    
    // ✨ Derived query methods - NO @Query needed!
    User findByUsername(String username);
    List<User> findByEmailContaining(String email);
    List<User> findByDepartmentAndActive(String department, Boolean active);
    List<User> findByAgeGreaterThan(Integer age);
    List<User> findTop10ByOrderByCreatedAtDesc();
    
    // Custom queries using @Query annotation (for complex cases)
    @Query("SELECT FROM User WHERE firstName LIKE :pattern OR lastName LIKE :pattern")
    List<User> searchByName(@Param("pattern") String pattern);
    
    @Query("SELECT expand(out('KNOWS')) FROM User WHERE @rid = :userId")
    List<User> findFriends(@Param("userId") ORID userId);
}
```

### Using Repositories

Inject and use repositories in your services:

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }
    
    public List<User> searchUsers(String query) {
        return userRepository.searchByName("%" + query + "%");
    }
    
    public Optional<User> findById(ORID id) {
        return userRepository.findById(id);
    }
    
    public List<User> getFriends(ORID userId) {
        return userRepository.findFriends(userId);
    }
}
```

### Using OrientDBTemplate

For more flexible operations, use `OrientDBTemplate`:

```java
@Service
public class CustomUserService {
    
    @Autowired
    private OrientDBTemplate orientDBTemplate;
    
    public User findUserByEmail(String email) {
        String query = "SELECT FROM User WHERE email = ?";
        return orientDBTemplate.querySingle(query, User.class, email).orElse(null);
    }
    
    public List<User> findActiveUsers() {
        String query = "SELECT FROM User WHERE active = true ORDER BY username";
        return orientDBTemplate.query(query, User.class);
    }
    
    public void createEdgeBetweenUsers(ORID userId1, ORID userId2) {
        orientDBTemplate.execute(session -> {
            String command = "CREATE EDGE KNOWS FROM ? TO ?";
            session.command(command, userId1, userId2);
            session.commit();
            return null;
        });
    }
}
```

## Annotations Reference

### @Vertex

Marks a class as an OrientDB vertex entity:

```java
@Vertex("User")  // Maps to OrientDB vertex class "User"
public class User { }
```

### @Edge

Defines relationships (edges) between vertices:

```java
@Edge(type = "KNOWS", direction = Direction.OUTGOING)
private List<User> friends;

@Edge(type = "BELONGS_TO", direction = Direction.INCOMING)
private Organization organization;
```

### @Id

Marks the identifier field (typically ORID):

```java
@Id
private ORID id;
```

### @Property

Customizes property name mapping:

```java
@Property("user_name")
private String username;
```

### @Version

Enables optimistic locking:

```java
@Version
private Integer version;
```

## Comparison with Manual OrientDB Usage

### Before (Manual OrientDB)

```java
@Repository
public class UserRepository extends OrientDBRepository<User> {
    
    @Override
    protected String getVertexClassName() {
        return "User";
    }
    
    @Override
    protected User fromVertex(OVertex vertex) {
        User user = new User();
        user.set_id(vertex.getIdentity());
        user.setUsername(vertex.getProperty("username"));
        user.setEmail(vertex.getProperty("email"));
        // Manual mapping for all properties...
        return user;
    }
    
    @Override
    protected OVertex toVertex(User entity, OVertex vertex) {
        vertex.setProperty("username", entity.getUsername());
        vertex.setProperty("email", entity.getEmail());
        // Manual mapping for all properties...
        return vertex;
    }
    
    public User findByEmail(String email) {
        String query = "SELECT FROM User WHERE email = ?";
        return executeQuerySingle(query, email).orElse(null);
    }
}
```

### After (Spring Data OrientDB)

```java
public interface UserRepository extends OrientDBRepository<User, ORID> {
    
    @Query("SELECT FROM User WHERE email = :email")
    User findByEmail(@Param("email") String email);
    
    // That's it! Everything else is handled automatically
}

@Vertex("User")
public class User {
    @Id private ORID id;
    private String username;
    private String email;
    // No manual mapping code needed!
}
```

## Key Benefits

1. **Less Boilerplate**: No need to write manual vertex-to-entity conversion code
2. **Annotation-Driven**: Simple, declarative mapping using familiar Spring Data annotations
3. **Query Methods**: Derive queries from method names or use `@Query` annotation
4. **Type Safety**: Compile-time checking for your domain model
5. **Spring Integration**: Seamless integration with Spring's dependency injection and transaction management
6. **Familiar API**: If you know Spring Data JPA or Spring Data Neo4j, you already know this!

## Advanced Features

### Transaction Management

Spring Data OrientDB integrates with Spring's transaction management:

```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public void createUsers(List<User> users) {
        // All saves in one transaction
        userRepository.saveAll(users);
    }
}
```

### Custom Repository Implementations

Extend repositories with custom implementations:

```java
public interface UserRepositoryCustom {
    List<User> complexQuery();
}

public class UserRepositoryImpl implements UserRepositoryCustom {
    
    @Autowired
    private OrientDBTemplate template;
    
    @Override
    public List<User> complexQuery() {
        // Custom implementation
        return template.query("SELECT ...", User.class);
    }
}

public interface UserRepository extends OrientDBRepository<User, ORID>, UserRepositoryCustom {
    // Combined interface
}
```

## What's New in Latest Version

### 🎊 v1.3.0-RELEASE - 100% FEATURE COMPLETE! 🎊
- ✅ **Event Callbacks**: @PrePersist, @PostLoad, @PreRemove lifecycle hooks
- ✅ **Schema Generation**: Automatic vertex class and property creation from entities
- ✅ **Projections**: Interface-based and class-based DTOs
- ✅ **100% Complete**: Full parity with Spring Data Neo4j/JPA core features!

### 🎯 Phase 3 (v1.2.0)
- ✅ **Auditing Support**: `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`
- ✅ **Automatic Timestamps**: No manual date setting required
- ✅ **User Tracking**: Audit who created/modified entities

See [PHASE3_FEATURES.md](PHASE3_FEATURES.md) for examples!

### 🚀 Phase 2 (v1.1.0)
- ✅ **Query Method Derivation**: Just write method names like `findByUsername(String name)` - queries auto-generated!
- ✅ **Named Queries**: Define queries in `orientdb-named-queries.properties` files
- ✅ **60-70% Code Reduction**: No more @Query annotations for simple queries!

See [PHASE2_FEATURES.md](PHASE2_FEATURES.md) for comprehensive examples!

### ✅ Phase 1 (v1.0.1)
- ✅ **Pagination Support**: Use `PageRequest` with `findAll(Pageable)`
- ✅ **Sorting Support**: Multi-field sorting with `Sort`
- ✅ **Query By Example**: Dynamic queries with `Example<T>`

See [PHASE1_FEATURES.md](PHASE1_FEATURES.md) for detailed examples!

## Roadmap

### ✅ 100% COMPLETE!
- ✅ Core CRUD operations
- ✅ Custom queries (@Query)
- ✅ Pagination & Sorting
- ✅ Query By Example
- ✅ Query method derivation
- ✅ Named queries
- ✅ Auditing support (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
- ✅ **Event callbacks** (@PrePersist, @PostLoad, @PreRemove)
- ✅ **Schema generation** (automatic from entities)
- ✅ **Projections** (interface & class-based DTOs)

### Future
- [ ] Reactive repository support (`ReactiveOrientDBRepository`)
- [ ] Specifications / Criteria API
- [ ] Event callbacks (@PrePersist, @PostLoad)
- [ ] Lazy loading strategies
- [ ] Spring Boot auto-configuration

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Acknowledgments

This library is inspired by and modeled after Spring Data Neo4j. Special thanks to the Spring Data team and the OrientDB community.

