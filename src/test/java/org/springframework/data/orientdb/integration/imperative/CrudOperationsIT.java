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
 * Integration tests for basic CRUD operations.
 * Tests the repository layer with real OrientDB database.
 */
@DisplayName("CRUD Operations Integration Tests")
class CrudOperationsIT extends OrientDBTestBase {

    @Autowired
    private TestPersonRepository repository;

    @BeforeEach
    void createSchema() {
        executeCommand("CREATE CLASS TestPerson IF NOT EXISTS EXTENDS V");
    }

    @Test
    @DisplayName("save() should persist entity and assign ID")
    void testSave() {
        // Given
        TestPerson person = new TestPerson("John", "Doe", 30);

        // When
        TestPerson saved = repository.save(person);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getAge()).isEqualTo(30);
        
        // Verify in database
        assertThat(countVertices("TestPerson")).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById() should retrieve entity")
    void testFindById() {
        // Given
        TestPerson person = repository.save(new TestPerson("Jane", "Smith", 25));

        // When
        Optional<TestPerson> found = repository.findById(person.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jane");
        assertThat(found.get().getAge()).isEqualTo(25);
    }

    @Test
    @DisplayName("findById() should return empty for non-existent ID")
    void testFindByIdNotFound() {
        // When
        Optional<TestPerson> found = repository.findById("#999:999");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll() should return all entities")
    void testFindAll() {
        // Given
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));
        repository.save(new TestPerson("Bob", "Johnson", 35));

        // When
        Iterable<TestPerson> all = repository.findAll();

        // Then
        assertThat(all).hasSize(3);
        assertThat(all)
            .extracting(TestPerson::getFirstName)
            .containsExactlyInAnyOrder("John", "Jane", "Bob");
    }

    @Test
    @DisplayName("update() should modify existing entity")
    void testUpdate() {
        // Given
        TestPerson person = repository.save(new TestPerson("John", "Doe", 30));
        String id = person.getId();

        // When
        person.setAge(31);
        person.setLastName("Smith");
        TestPerson updated = repository.save(person);

        // Then
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getAge()).isEqualTo(31);
        assertThat(updated.getLastName()).isEqualTo("Smith");
        
        // Verify in database
        TestPerson fromDb = repository.findById(id).orElseThrow();
        assertThat(fromDb.getAge()).isEqualTo(31);
        assertThat(fromDb.getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("delete() should remove entity")
    void testDelete() {
        // Given
        TestPerson person = repository.save(new TestPerson("John", "Doe", 30));
        String id = person.getId();

        // When
        repository.delete(person);

        // Then
        assertThat(repository.findById(id)).isEmpty();
        assertThat(countVertices("TestPerson")).isEqualTo(0L);
    }

    @Test
    @DisplayName("deleteById() should remove entity by ID")
    void testDeleteById() {
        // Given
        TestPerson person = repository.save(new TestPerson("Jane", "Smith", 25));
        String id = person.getId();

        // When
        repository.deleteById(id);

        // Then
        assertThat(repository.findById(id)).isEmpty();
        assertThat(countVertices("TestPerson")).isEqualTo(0L);
    }

    @Test
    @DisplayName("deleteAll() should remove all entities")
    void testDeleteAll() {
        // Given
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));
        repository.save(new TestPerson("Bob", "Johnson", 35));

        // When
        repository.deleteAll();

        // Then
        assertThat(repository.count()).isEqualTo(0L);
        assertThat(countVertices("TestPerson")).isEqualTo(0L);
    }

    @Test
    @DisplayName("count() should return total number of entities")
    void testCount() {
        // Given
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("existsById() should return true for existing entity")
    void testExistsByIdTrue() {
        // Given
        TestPerson person = repository.save(new TestPerson("John", "Doe", 30));

        // When
        boolean exists = repository.existsById(person.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById() should return false for non-existent entity")
    void testExistsByIdFalse() {
        // When
        boolean exists = repository.existsById("#999:999");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("saveAll() should persist multiple entities")
    void testSaveAll() {
        // Given
        List<TestPerson> people = List.of(
            new TestPerson("John", "Doe", 30),
            new TestPerson("Jane", "Smith", 25),
            new TestPerson("Bob", "Johnson", 35)
        );

        // When
        Iterable<TestPerson> saved = repository.saveAll(people);

        // Then
        assertThat(saved).hasSize(3);
        assertThat(repository.count()).isEqualTo(3L);
    }

    @Test
    @DisplayName("findAllById() should retrieve multiple entities by IDs")
    void testFindAllById() {
        // Given
        TestPerson person1 = repository.save(new TestPerson("John", "Doe", 30));
        TestPerson person2 = repository.save(new TestPerson("Jane", "Smith", 25));
        repository.save(new TestPerson("Bob", "Johnson", 35));

        // When
        Iterable<TestPerson> found = repository.findAllById(
            List.of(person1.getId(), person2.getId())
        );

        // Then
        assertThat(found).hasSize(2);
        assertThat(found)
            .extracting(TestPerson::getFirstName)
            .containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    @DisplayName("deleteAllById() should remove multiple entities")
    void testDeleteAllById() {
        // Given
        TestPerson person1 = repository.save(new TestPerson("John", "Doe", 30));
        TestPerson person2 = repository.save(new TestPerson("Jane", "Smith", 25));
        TestPerson person3 = repository.save(new TestPerson("Bob", "Johnson", 35));

        // When
        repository.deleteAllById(List.of(person1.getId(), person2.getId()));

        // Then
        assertThat(repository.count()).isEqualTo(1L);
        assertThat(repository.existsById(person3.getId())).isTrue();
    }
}

