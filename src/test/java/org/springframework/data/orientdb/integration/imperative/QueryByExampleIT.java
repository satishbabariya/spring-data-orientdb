package org.springframework.data.orientdb.integration.imperative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.orientdb.integration.shared.TestPerson;
import org.springframework.data.orientdb.integration.shared.TestPersonRepository;
import org.springframework.data.orientdb.test.OrientDBTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for Query By Example functionality.
 */
@DisplayName("Query By Example Integration Tests")
class QueryByExampleIT extends OrientDBTestBase {

    @Autowired
    private TestPersonRepository repository;

    @BeforeEach
    void setupTestData() {
        executeCommand("CREATE CLASS TestPerson IF NOT EXISTS EXTENDS V");
        
        // Create diverse test data
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Doe", 25));
        repository.save(new TestPerson("Bob", "Smith", 30));
        repository.save(new TestPerson("Alice", "Smith", 28));
        
        // Set some inactive
        TestPerson person = repository.findByFirstName("Bob").orElseThrow();
        person.setActive(false);
        repository.save(person);
    }

    @Test
    @DisplayName("findAll(Example) should find by single field")
    void testFindAllBySingleField() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setFirstName("John");
        Example<TestPerson> example = Example.of(probe);

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("findAll(Example) should find by multiple fields")
    void testFindAllByMultipleFields() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setLastName("Smith");
        probe.setActive(true);
        Example<TestPerson> example = Example.of(probe);

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("findOne(Example) should return single match")
    void testFindOne() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setFirstName("Alice");
        Example<TestPerson> example = Example.of(probe);

        // When
        Optional<TestPerson> result = repository.findOne(example);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getLastName()).isEqualTo("Smith");
        assertThat(result.get().getAge()).isEqualTo(28);
    }

    @Test
    @DisplayName("findOne(Example) should return empty for no match")
    void testFindOneNoMatch() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setFirstName("NonExistent");
        Example<TestPerson> example = Example.of(probe);

        // When
        Optional<TestPerson> result = repository.findOne(example);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("exists(Example) should return true for match")
    void testExistsTrue() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setAge(30);
        Example<TestPerson> example = Example.of(probe);

        // When
        boolean exists = repository.exists(example);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("exists(Example) should return false for no match")
    void testExistsFalse() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setAge(999);
        Example<TestPerson> example = Example.of(probe);

        // When
        boolean exists = repository.exists(example);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("count(Example) should count matching entities")
    void testCount() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setLastName("Doe");
        Example<TestPerson> example = Example.of(probe);

        // When
        long count = repository.count(example);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("findAll(Example) with boolean field should work")
    void testFindAllByBoolean() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setActive(false);
        Example<TestPerson> example = Example.of(probe);

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("findAll(Example) with null fields should match all")
    void testFindAllWithNullFields() {
        // Given
        TestPerson probe = new TestPerson(); // All fields null
        Example<TestPerson> example = Example.of(probe);

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example);

        // Then
        assertThat(results).hasSize(4); // All test people
    }

    @Test
    @DisplayName("findAll(Example) with complex criteria")
    void testFindAllComplexCriteria() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setAge(30);
        probe.setActive(true);
        Example<TestPerson> example = Example.of(probe);

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("John");
    }
}

