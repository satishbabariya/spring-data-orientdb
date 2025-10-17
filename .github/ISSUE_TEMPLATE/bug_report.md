---
name: Bug Report
about: Create a report to help us improve
title: '[BUG] '
labels: bug
assignees: ''
---

## Bug Description
A clear and concise description of what the bug is.

## Steps to Reproduce
Steps to reproduce the behavior:
1. Go to '...'
2. Create entity '...'
3. Call repository method '...'
4. See error

## Expected Behavior
A clear and concise description of what you expected to happen.

## Actual Behavior
A clear and concise description of what actually happened.

## Code Sample
```java
// Minimal code sample that reproduces the issue
@Vertex("Person")
public class Person {
    @Id
    private ORID id;
    private String name;
}

// Repository
public interface PersonRepository extends OrientDBRepository<Person, ORID> {
    List<Person> findByName(String name);
}

// Usage that causes the issue
personRepository.findByName("John");
```

## Stack Trace
```
// If applicable, add the full stack trace
```

## Environment
- **Spring Data OrientDB Version**: [e.g., 0.0.1-SNAPSHOT]
- **Spring Boot Version**: [e.g., 3.3.5]
- **OrientDB Version**: [e.g., 3.2.32]
- **Java Version**: [e.g., 17]
- **OS**: [e.g., Ubuntu 22.04, macOS 14, Windows 11]

## Additional Context
Add any other context about the problem here. This may include:
- Database configuration
- Special setup requirements
- Related issues
- Workarounds you've tried

## Possible Solution
If you have suggestions on how to fix the bug, please describe them here.

## Checklist
- [ ] I have searched existing issues to ensure this is not a duplicate
- [ ] I have provided a minimal code sample that reproduces the issue
- [ ] I have included the full stack trace (if applicable)
- [ ] I have specified my environment details

