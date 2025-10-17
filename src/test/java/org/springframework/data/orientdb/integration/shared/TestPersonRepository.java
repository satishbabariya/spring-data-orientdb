package org.springframework.data.orientdb.integration.shared;

import org.springframework.data.orientdb.repository.OrientDBRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Test repository for TestPerson entity.
 * Used across multiple integration tests.
 */
@Repository
public interface TestPersonRepository extends OrientDBRepository<TestPerson, String> {
    
    // Query derivation tests
    Optional<TestPerson> findByFirstName(String firstName);
    
    List<TestPerson> findByLastName(String lastName);
    
    List<TestPerson> findByAgeGreaterThan(Integer age);
    
    List<TestPerson> findByAgeLessThan(Integer age);
    
    List<TestPerson> findByActiveTrue();
    
    List<TestPerson> findByActiveFalse();
    
    List<TestPerson> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<TestPerson> findByFirstNameOrLastName(String firstName, String lastName);
    
    List<TestPerson> findByAgeBetween(Integer minAge, Integer maxAge);
    
    List<TestPerson> findByFirstNameContaining(String substring);
    
    List<TestPerson> findByFirstNameStartingWith(String prefix);
    
    List<TestPerson> findByFirstNameEndingWith(String suffix);
    
    // Count queries
    long countByActive(Boolean active);
    
    long countByAgeGreaterThan(Integer age);
    
    // Exists queries
    boolean existsByFirstName(String firstName);
    
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    
    // Delete queries
    long deleteByActive(Boolean active);
    
    void deleteByFirstName(String firstName);
}

