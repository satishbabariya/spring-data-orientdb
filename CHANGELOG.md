# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial implementation of Spring Data OrientDB
- Core repository abstractions
- Query method derivation
- Transaction management support
- Pagination and sorting
- Query By Example (QBE)
- Custom @Query annotation support
- Entity lifecycle callbacks (@PrePersist, @PostLoad, @PreRemove)
- Auditing support (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy)
- Schema generation from entities
- Projection support (interface and class-based)
- Observability and metrics integration
- Async repository support
- Caching integration
- Comprehensive test suite
- Complete documentation

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

### Security
- N/A

## [0.0.1-SNAPSHOT] - 2024-01-15

### Added
- Initial project setup
- Maven POM configuration
- Basic project structure
- License (Apache 2.0)
- README with project overview
- Core infrastructure classes:
  - OrientDBMappingContext
  - OrientDBSimpleTypes
  - OrientDBTemplate
  - OrientDBOperations interface
- Entity mapping support:
  - OrientDBPersistentEntity
  - OrientDBPersistentProperty
  - Mapping event listeners
- Entity conversion layer:
  - OrientDBEntityConverter
  - OrientDBProjectionConverter
- Schema generation:
  - SchemaGenerator
  - @Vertex, @Edge, @Property annotations
  - @Id, @Version annotations
  - Auditing annotations
  - Lifecycle callback annotations
- Spring configuration:
  - AbstractOrientDBConfiguration
  - EnableOrientDBRepositories
  - EnableOrientDBAuditing
  - EnableOrientDBCaching
  - EnableOrientDBObservability
  - EnableOrientDBTransactionManagement
- Repository infrastructure:
  - OrientDBRepository interface
  - AsyncOrientDBRepository interface
  - SimpleOrientDBRepository implementation
  - SimpleAsyncOrientDBRepository implementation
  - OrientDBRepositoryFactory
  - OrientDBRepositoryFactoryBean
- Query support:
  - @Query annotation
  - OrientDBQueryMethod
  - OrientDBQueryCreator
  - PartTreeOrientDBQuery
  - StringBasedOrientDBQuery
  - Query method derivation with 30+ keywords
  - Named queries support
- Transaction management:
  - OrientDBTransactionManager
  - SessionHolder
  - Transaction synchronization
- Observability:
  - OrientDBMetrics
  - Micrometer integration
- Testing infrastructure:
  - LibraryBuildTest
  - Integration tests for CRUD operations
  - Pagination and sorting tests
  - Query By Example tests
  - Query derivation tests
  - Transaction management tests
  - Test configuration and utilities
- GitHub Actions workflows:
  - CI pipeline (multi-OS, multi-JDK)
  - Release automation
  - Pull request checks
  - Dependency updates
  - Code coverage reporting
- Project documentation:
  - Comprehensive README
  - CONTRIBUTING.md
  - Issue templates
  - Pull request template
  - Dependabot configuration
- Distribution management:
  - GitHub Packages configuration
  - Release process automation

---

## Version History

### Version Numbering

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version: Incompatible API changes
- **MINOR** version: Added functionality (backward compatible)
- **PATCH** version: Bug fixes (backward compatible)

### Release Tags

- `v0.0.1-SNAPSHOT`: Initial development snapshot

---

## Links

- [Repository](https://github.com/satishbabariya/spring-data-orientdb)
- [Issues](https://github.com/satishbabariya/spring-data-orientdb/issues)
- [Releases](https://github.com/satishbabariya/spring-data-orientdb/releases)
- [Contributing Guide](CONTRIBUTING.md)

---

**Note**: This is a community-driven project and is not officially supported by the Spring team.

