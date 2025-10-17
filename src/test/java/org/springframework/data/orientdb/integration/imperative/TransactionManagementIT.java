package org.springframework.data.orientdb.integration.imperative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.orientdb.integration.shared.TestPerson;
import org.springframework.data.orientdb.integration.shared.TestPersonRepository;
import org.springframework.data.orientdb.test.OrientDBTestBase;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for transaction management.
 * Tests @Transactional behavior, commits, and rollbacks.
 */
@DisplayName("Transaction Management Integration Tests")
class TransactionManagementIT extends OrientDBTestBase {

    @Autowired
    private TestPersonRepository repository;

    @BeforeEach
    void setupSchema() {
        executeCommand("CREATE CLASS TestPerson IF NOT EXISTS EXTENDS V");
    }

    @Test
    @Transactional
    @DisplayName("Transaction commit should persist all changes")
    void testTransactionCommit() {
        // Given & When
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));

        // Then - within transaction
        assertThat(repository.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Transaction commit should be visible after method completes")
    void testTransactionCommitVisible() {
        // When
        saveInTransaction();

        // Then - after transaction commits
        assertThat(repository.count()).isEqualTo(2L);
    }

    @Transactional
    void saveInTransaction() {
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));
    }

    @Test
    @DisplayName("Transaction rollback should discard all changes")
    void testTransactionRollback() {
        // Given
        long initialCount = repository.count();

        // When
        try {
            saveWithException();
        } catch (RuntimeException e) {
            // Expected
        }

        // Then - changes should be rolled back
        assertThat(repository.count()).isEqualTo(initialCount);
    }

    @Transactional
    void saveWithException() {
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));
        throw new RuntimeException("Simulated error");
    }

    @Test
    @DisplayName("Partial transaction should rollback completely")
    void testPartialTransactionRollback() {
        // Given
        repository.save(new TestPerson("Existing", "Person", 40));
        long initialCount = repository.count();

        // When
        try {
            saveTwoWithException();
        } catch (RuntimeException e) {
            // Expected
        }

        // Then - should still have only the initial person
        assertThat(repository.count()).isEqualTo(initialCount);
    }

    @Transactional
    void saveTwoWithException() {
        repository.save(new TestPerson("John", "Doe", 30));
        repository.save(new TestPerson("Jane", "Smith", 25));
        // Simulate error after saving 2
        throw new RuntimeException("Error after 2 saves");
    }

    @Test
    @Transactional
    @DisplayName("Multiple operations in transaction should all commit together")
    void testMultipleOperationsInTransaction() {
        // Given
        TestPerson person1 = repository.save(new TestPerson("John", "Doe", 30));
        TestPerson person2 = repository.save(new TestPerson("Jane", "Smith", 25));

        // When - update within same transaction
        person1.setAge(31);
        repository.save(person1);
        
        repository.deleteById(person2.getId());
        
        TestPerson person3 = repository.save(new TestPerson("Bob", "Johnson", 35));

        // Then
        assertThat(repository.count()).isEqualTo(2L);
        assertThat(repository.findById(person1.getId())).isPresent();
        assertThat(repository.findById(person2.getId())).isEmpty();
        assertThat(repository.findById(person3.getId())).isPresent();
    }

    @Test
    @DisplayName("Nested-like transaction behavior should work")
    void testNestedTransactions() {
        // When
        outerTransaction();

        // Then
        assertThat(repository.count()).isEqualTo(3L);
    }

    @Transactional
    void outerTransaction() {
        repository.save(new TestPerson("Outer1", "Person", 30));
        innerTransaction();
        repository.save(new TestPerson("Outer2", "Person", 35));
    }

    @Transactional
    void innerTransaction() {
        repository.save(new TestPerson("Inner", "Person", 25));
    }

    @Test
    @DisplayName("Transaction with delete operations should work")
    void testTransactionWithDelete() {
        // Given
        TestPerson person1 = repository.save(new TestPerson("John", "Doe", 30));
        TestPerson person2 = repository.save(new TestPerson("Jane", "Smith", 25));
        
        // When
        deleteInTransaction(person1.getId());

        // Then
        assertThat(repository.existsById(person1.getId())).isFalse();
        assertThat(repository.existsById(person2.getId())).isTrue();
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Transactional
    void deleteInTransaction(String id) {
        repository.deleteById(id);
    }

    @Test
    @DisplayName("Rollback should restore deleted entities")
    void testRollbackRestoresDeleted() {
        // Given
        TestPerson person = repository.save(new TestPerson("Important", "Person", 30));
        String id = person.getId();
        
        // When
        try {
            deleteWithException(id);
        } catch (RuntimeException e) {
            // Expected
        }

        // Then - person should still exist
        assertThat(repository.existsById(id)).isTrue();
    }

    @Transactional
    void deleteWithException(String id) {
        repository.deleteById(id);
        throw new RuntimeException("Rollback delete");
    }
}

