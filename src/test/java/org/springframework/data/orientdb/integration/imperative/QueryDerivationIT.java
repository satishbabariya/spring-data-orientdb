package org.springframework.data.orientdb.integration.imperative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.orientdb.integration.shared.TestPerson;
import org.springframework.data.orientdb.integration.shared.TestPersonRepository;
import org.springframework.data.orientdb.test.OrientDBTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for query method derivation.
 * Tests findBy*, countBy*, existsBy*, deleteBy* methods.
 */
@DisplayName("Query Derivation Integration Tests")
class QueryDerivationIT extends OrientDBTestBase {

    @Autowired
    private TestPersonRepository repository;

    @BeforeEach
    void setupTestData() {
        executeCommand("CREATE CLASS TestPerson IF NOT EXISTS EXTENDS V");
        
        // Create test data
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Doe", 25));
        repository.save(new TestPerson("Bob", "Smith", 35));
        repository.save(new TestPerson("Alice", "Johnson", 28));
        repository.save(new TestPerson("Charlie", "Brown", 40));
        
        // Set some inactive
        TestPerson person = repository.findByFirstName("Charlie").orElseThrow();
        person.setActive(false);
        repository.save(person);
    }

    @Test
    @DisplayName("findByFirstName() should find person by first name")
    void testFindByFirstName() {
        // When
        Optional<TestPerson> found = repository.findByFirstName("John");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("findByLastName() should find all people with same last name")
    void testFindByLastName() {
        // When
        List<TestPerson> found = repository.findByLastName("Doe");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getFirstName)
            .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    @DisplayName("findByAgeGreaterThan() should find people older than specified age")
    void testFindByAgeGreaterThan() {
        // When
        List<TestPerson> found = repository.findByAgeGreaterThan(30);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getAge)
            .allMatch(age -> age > 30);
    }

    @Test
    @DisplayName("findByAgeLessThan() should find people younger than specified age")
    void testFindByAgeLessThan() {
        // When
        List<TestPerson> found = repository.findByAgeLessThan(30);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getAge)
            .allMatch(age -> age < 30);
    }

    @Test
    @DisplayName("findByActiveTrue() should find all active people")
    void testFindByActiveTrue() {
        // When
        List<TestPerson> found = repository.findByActiveTrue();

        // Then
        assertThat(found).hasSize(4);
        assertThat(found)
            .extracting(TestPerson::getActive)
            .allMatch(active -> active == true);
    }

    @Test
    @DisplayName("findByActiveFalse() should find all inactive people")
    void testFindByActiveFalse() {
        // When
        List<TestPerson> found = repository.findByActiveFalse();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("findByFirstNameAndLastName() should find person by both names")
    void testFindByFirstNameAndLastName() {
        // When
        List<TestPerson> found = repository.findByFirstNameAndLastName("John", "Doe");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("findByFirstNameOrLastName() should find people matching either name")
    void testFindByFirstNameOrLastName() {
        // When
        List<TestPerson> found = repository.findByFirstNameOrLastName("John", "Smith");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getFirstName)
            .containsExactlyInAnyOrder("John", "Bob");
    }

    @Test
    @DisplayName("findByAgeBetween() should find people in age range")
    void testFindByAgeBetween() {
        // When
        List<TestPerson> found = repository.findByAgeBetween(25, 30);

        // Then
        assertThat(found).hasSize(3);
        assertThat(found)
            .extracting(TestPerson::getAge)
            .allMatch(age -> age >= 25 && age <= 30);
    }

    @Test
    @DisplayName("findByFirstNameContaining() should find people with substring in name")
    void testFindByFirstNameContaining() {
        // When
        List<TestPerson> found = repository.findByFirstNameContaining("oh");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("findByFirstNameStartingWith() should find people with name prefix")
    void testFindByFirstNameStartingWith() {
        // When
        List<TestPerson> found = repository.findByFirstNameStartingWith("J");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getFirstName)
            .allMatch(name -> name.startsWith("J"));
    }

    @Test
    @DisplayName("findByFirstNameEndingWith() should find people with name suffix")
    void testFindByFirstNameEndingWith() {
        // When
        List<TestPerson> found = repository.findByFirstNameEndingWith("e");

        // Then
        assertThat(found).hasSize(3); // Jane, Alice, Charlie
        assertThat(found)
            .extracting(TestPerson::getFirstName)
            .allMatch(name -> name.endsWith("e"));
    }

    @Test
    @DisplayName("countByActive() should count active/inactive people")
    void testCountByActive() {
        // When
        long activeCount = repository.countByActive(true);
        long inactiveCount = repository.countByActive(false);

        // Then
        assertThat(activeCount).isEqualTo(4L);
        assertThat(inactiveCount).isEqualTo(1L);
    }

    @Test
    @DisplayName("countByAgeGreaterThan() should count people older than age")
    void testCountByAgeGreaterThan() {
        // When
        long count = repository.countByAgeGreaterThan(30);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("existsByFirstName() should return true for existing name")
    void testExistsByFirstName() {
        // When
        boolean exists = repository.existsByFirstName("John");
        boolean notExists = repository.existsByFirstName("NonExistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("existsByFirstNameAndLastName() should check existence by both names")
    void testExistsByFirstNameAndLastName() {
        // When
        boolean exists = repository.existsByFirstNameAndLastName("John", "Doe");
        boolean notExists = repository.existsByFirstNameAndLastName("John", "Smith");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("deleteByActive() should delete all inactive people")
    void testDeleteByActive() {
        // Given
        long initialCount = repository.count();

        // When
        long deleted = repository.deleteByActive(false);

        // Then
        assertThat(deleted).isEqualTo(1L);
        assertThat(repository.count()).isEqualTo(initialCount - 1);
        assertThat(repository.findByActiveFalse()).isEmpty();
    }

    @Test
    @DisplayName("deleteByFirstName() should delete person by first name")
    void testDeleteByFirstName() {
        // Given
        long initialCount = repository.count();

        // When
        repository.deleteByFirstName("Bob");

        // Then
        assertThat(repository.count()).isEqualTo(initialCount - 1);
        assertThat(repository.findByFirstName("Bob")).isEmpty();
    }
}

