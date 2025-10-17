# Contributing to Spring Data OrientDB

Thank you for your interest in contributing to Spring Data OrientDB! This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [CI/CD Pipeline](#cicd-pipeline)
- [Release Process](#release-process)

## 📜 Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code. Please be respectful and constructive in all interactions.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/spring-data-orientdb.git
   cd spring-data-orientdb
   ```

3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/satishbabariya/spring-data-orientdb.git
   ```

### Build the Project

```bash
# Build without tests
mvn clean install -DskipTests

# Build with tests
mvn clean install

# Run tests only
mvn test

# Run integration tests
mvn verify
```

## 🔄 Development Workflow

### 1. Create a Feature Branch

Always create a new branch for your work:

```bash
git checkout -b feature/my-feature
# or
git checkout -b fix/bug-description
# or
git checkout -b docs/documentation-update
```

### Branch Naming Convention

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions or modifications
- `chore/` - Maintenance tasks

### 2. Make Changes

- Write clean, readable code
- Follow existing code style
- Add tests for new features
- Update documentation as needed
- Keep commits atomic and focused

### 3. Commit Your Changes

Follow conventional commit message format:

```bash
git commit -m "feat: add new feature"
git commit -m "fix: resolve bug in query method"
git commit -m "docs: update README with examples"
git commit -m "test: add integration tests for repositories"
git commit -m "refactor: improve code structure"
git commit -m "chore: update dependencies"
```

**Commit Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `perf`: Performance improvements
- `ci`: CI/CD changes

### 4. Keep Your Branch Updated

```bash
git fetch upstream
git rebase upstream/main
```

### 5. Push Your Changes

```bash
git push origin feature/my-feature
```

## 📥 Pull Request Process

### Before Submitting

1. **Test your changes**:
   ```bash
   mvn clean verify
   ```

2. **Check code coverage**:
   ```bash
   mvn jacoco:check
   ```

3. **Ensure code quality**:
   - No compiler warnings
   - Code follows project conventions
   - All tests pass
   - Coverage meets minimum threshold (70%)

### Submitting a Pull Request

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. Select your feature branch
4. Fill in the PR template:
   - **Title**: Clear, concise description
   - **Description**: What changes were made and why
   - **Related Issues**: Link to any related issues
   - **Testing**: How you tested the changes
   - **Screenshots**: If applicable

### PR Template Example

```markdown
## Description
Brief description of the changes

## Related Issues
Fixes #123

## Changes Made
- Added new feature X
- Fixed bug in Y
- Updated documentation for Z

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed
- [ ] All tests pass locally

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings introduced
- [ ] Tests added for new features
- [ ] All tests pass
```

### PR Review Process

1. **Automated Checks**: CI pipeline will run automatically
   - Build verification
   - All tests (unit + integration)
   - Code coverage check
   - Multi-platform testing (Ubuntu, macOS, Windows)
   - Multi-JDK testing (Java 17, 21)

2. **Code Review**: Maintainers will review your PR
   - Address feedback promptly
   - Update PR based on review comments
   - Keep discussion constructive

3. **Approval**: Once approved and CI passes, PR will be merged

## 💻 Coding Standards

### Java Style Guide

- Follow [Spring Framework Code Style](https://github.com/spring-projects/spring-framework/wiki/Code-Style)
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Add Javadoc for public APIs

### Code Organization

```java
// 1. Package declaration
package org.springframework.data.orientdb.repository;

// 2. Imports (organized)
import java.util.List;
import org.springframework.data.repository.CrudRepository;

// 3. Class Javadoc
/**
 * Repository interface for OrientDB entities.
 * 
 * @author Your Name
 * @since 1.0.0
 */
// 4. Class declaration
public interface OrientDBRepository<T, ID> extends CrudRepository<T, ID> {
    
    // 5. Methods (logically grouped)
    List<T> findByProperty(String value);
}
```

### Best Practices

- **DRY**: Don't Repeat Yourself
- **SOLID**: Follow SOLID principles
- **Null Safety**: Use `Optional` where appropriate
- **Immutability**: Prefer immutable objects
- **Exception Handling**: Use appropriate exception types
- **Resource Management**: Use try-with-resources
- **Logging**: Use appropriate log levels

## 🧪 Testing Guidelines

### Test Structure

```
src/test/java/
├── org/springframework/data/orientdb/
│   ├── unit/              # Unit tests
│   ├── integration/       # Integration tests
│   └── test/              # Test utilities
```

### Writing Tests

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersonRepositoryIT {
    
    @Autowired
    private PersonRepository repository;
    
    @Test
    void shouldSaveAndFindPerson() {
        // Given
        Person person = new Person("John", "Doe");
        
        // When
        Person saved = repository.save(person);
        Optional<Person> found = repository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }
}
```

### Test Coverage

- Aim for **70%+ code coverage**
- Focus on critical paths
- Test edge cases and error conditions
- Use meaningful test names
- One assertion concept per test

### Running Specific Tests

```bash
# Run all unit tests (default)
mvn test

# Run specific test class
mvn test -Dtest=PersonRepositoryTest

# Run specific test method
mvn test -Dtest=PersonRepositoryTest#shouldSaveAndFindPerson

# Run integration tests (requires OrientDB)
mvn verify

# Run integration tests only (skip unit tests)
mvn verify -DskipUnitTests

# Run with coverage
mvn clean test jacoco:report
```

### Integration Tests

Integration tests (`*IT.java`) require OrientDB to be running and are **skipped by default in CI** to ensure fast, reliable builds. They should be run locally during development.

**Why are integration tests skipped in CI?**
- Require OrientDB database setup
- Environment-specific configurations
- Longer execution time
- Platform-specific issues (especially on macOS/Windows)

**Running integration tests locally:**
```bash
# Run all tests including integration tests
mvn verify

# Run only integration tests
mvn failsafe:integration-test

# Skip integration tests (default in CI)
mvn test -DskipITs
```

## 🔄 CI/CD Pipeline

### Automated Workflows

#### 1. **CI Workflow** (`.github/workflows/ci.yml`)
Runs on every push and pull request:
- Multi-OS testing (Ubuntu, macOS, Windows)
- Multi-JDK testing (Java 17, 21)
- Unit tests only (integration tests skipped for reliability)
- Code coverage analysis
- Test result archival

#### 2. **PR Check Workflow** (`.github/workflows/pr-check.yml`)
Runs on pull requests:
- Build and test verification
- Code coverage reporting
- PR size labeling
- Automatic PR labeling

#### 3. **Dependency Update Workflow** (`.github/workflows/dependency-update.yml`)
Runs weekly:
- Checks for dependency updates
- Creates PR with updates
- Automatic testing of updates

#### 4. **Release Workflow** (`.github/workflows/release.yml`)
Runs on version tags:
- Creates GitHub release
- Publishes to GitHub Packages
- Uploads release artifacts

### Viewing CI Results

1. Go to your PR on GitHub
2. Scroll to "Checks" section
3. View detailed logs for any failures
4. Fix issues and push updates

### Local CI Simulation

```bash
# Run full CI pipeline locally
mvn clean verify

# Check code coverage
mvn jacoco:check

# Display dependency updates
mvn versions:display-dependency-updates
```

## 📦 Release Process

### Version Numbering

We follow [Semantic Versioning](https://semver.org/):
- **Major** (1.0.0): Breaking changes
- **Minor** (0.1.0): New features, backward compatible
- **Patch** (0.0.1): Bug fixes, backward compatible

### Creating a Release

1. **Update version in pom.xml**:
   ```bash
   mvn versions:set -DnewVersion=1.0.0
   ```

2. **Commit version change**:
   ```bash
   git commit -am "chore: bump version to 1.0.0"
   ```

3. **Create and push tag**:
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

4. **GitHub Actions will automatically**:
   - Build the project
   - Run all tests
   - Create GitHub release
   - Publish to GitHub Packages
   - Upload artifacts

### Release Checklist

- [ ] All tests pass
- [ ] Documentation updated
- [ ] CHANGELOG updated
- [ ] Version bumped appropriately
- [ ] Tag created and pushed
- [ ] GitHub release verified
- [ ] Artifacts published

## 🏷️ Labels

### PR Labels

- `bug` - Bug fixes
- `enhancement` - New features
- `documentation` - Documentation updates
- `dependencies` - Dependency updates
- `ci/cd` - CI/CD related changes
- `tests` - Test additions/updates
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention needed
- `automated` - Automated PRs

### Size Labels

- `size/xs` - 0-10 lines changed
- `size/s` - 10-100 lines changed
- `size/m` - 100-500 lines changed
- `size/l` - 500-1000 lines changed
- `size/xl` - 1000+ lines changed

## 📞 Getting Help

### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: Questions and general discussion
- **Stack Overflow**: Tag `spring-data-orientdb`

### Asking Questions

When asking questions:
1. Search existing issues/discussions first
2. Provide context and details
3. Include code samples if relevant
4. Specify versions (Java, Spring Boot, OrientDB)

## 🎯 Issue Reporting

### Bug Reports

Include:
- Clear description of the issue
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details (Java version, OS, etc.)
- Code samples or test case
- Stack traces if applicable

### Feature Requests

Include:
- Clear description of the feature
- Use case and motivation
- Proposed API or usage example
- Alternative solutions considered

## 📚 Additional Resources

- [Spring Data Commons Documentation](https://docs.spring.io/spring-data/commons/docs/current/reference/html/)
- [OrientDB Documentation](https://orientdb.org/docs/3.2.x/)
- [Spring Framework Code Style](https://github.com/spring-projects/spring-framework/wiki/Code-Style)
- [Conventional Commits](https://www.conventionalcommits.org/)

## 🙏 Thank You

Thank you for contributing to Spring Data OrientDB! Your contributions help make this project better for everyone.

---

**Questions?** Open an issue or discussion, and we'll be happy to help!

