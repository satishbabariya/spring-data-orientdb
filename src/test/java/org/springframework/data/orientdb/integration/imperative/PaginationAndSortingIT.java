package org.springframework.data.orientdb.integration.imperative;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.orientdb.integration.shared.TestPerson;
import org.springframework.data.orientdb.integration.shared.TestPersonRepository;
import org.springframework.data.orientdb.test.OrientDBTestBase;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for pagination and sorting.
 */
@DisplayName("Pagination and Sorting Integration Tests")
class PaginationAndSortingIT extends OrientDBTestBase {

    @Autowired
    private TestPersonRepository repository;

    @BeforeEach
    void setupTestData() {
        executeCommand("CREATE CLASS TestPerson IF NOT EXISTS EXTENDS V");
        
        // Create 25 test people
        for (int i = 1; i <= 25; i++) {
            TestPerson person = new TestPerson(
                "Person" + i,
                "LastName" + (i % 5),
                20 + (i % 30)
            );
            repository.save(person);
        }
    }

    @Test
    @DisplayName("findAll(Pageable) should return first page")
    void testFindAllFirstPage() {
        // When
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<TestPerson> page = repository.findAll(pageRequest);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(25L);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("findAll(Pageable) should return second page")
    void testFindAllSecondPage() {
        // When
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<TestPerson> page = repository.findAll(pageRequest);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("findAll(Pageable) should return last page with remaining items")
    void testFindAllLastPage() {
        // When
        PageRequest pageRequest = PageRequest.of(2, 10);
        Page<TestPerson> page = repository.findAll(pageRequest);

        // Then
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.hasNext()).isFalse();
        assertThat(page.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("findAll(Sort) should sort by firstName ascending")
    void testFindAllSortAscending() {
        // When
        Sort sort = Sort.by(Sort.Direction.ASC, "firstName");
        List<TestPerson> sorted = (List<TestPerson>) repository.findAll(sort);

        // Then
        assertThat(sorted).hasSize(25);
        assertThat(sorted.get(0).getFirstName()).isEqualTo("Person1");
        assertThat(sorted.get(sorted.size() - 1).getFirstName()).isEqualTo("Person9");
    }

    @Test
    @DisplayName("findAll(Sort) should sort by age descending")
    void testFindAllSortDescending() {
        // When
        Sort sort = Sort.by(Sort.Direction.DESC, "age");
        List<TestPerson> sorted = (List<TestPerson>) repository.findAll(sort);

        // Then
        assertThat(sorted).hasSize(25);
        // Verify descending order
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertThat(sorted.get(i).getAge())
                .isGreaterThanOrEqualTo(sorted.get(i + 1).getAge());
        }
    }

    @Test
    @DisplayName("findAll(Pageable with Sort) should combine pagination and sorting")
    void testFindAllPageableWithSort() {
        // When
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "age"));
        Page<TestPerson> page = repository.findAll(pageRequest);

        // Then
        assertThat(page.getContent()).hasSize(10);
        // Verify first page has highest ages
        assertThat(page.getContent().get(0).getAge())
            .isGreaterThanOrEqualTo(page.getContent().get(9).getAge());
    }

    @Test
    @DisplayName("findAll(Example, Pageable) should filter and paginate")
    void testFindAllExamplePageable() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setActive(true);
        Example<TestPerson> example = Example.of(probe);

        // When
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<TestPerson> page = repository.findAll(example, pageRequest);

        // Then
        assertThat(page.getContent()).hasSizeGreaterThan(0);
        assertThat(page.getContent()).allMatch(TestPerson::getActive);
    }

    @Test
    @DisplayName("findAll(Example, Sort) should filter and sort")
    void testFindAllExampleSort() {
        // Given
        TestPerson probe = new TestPerson();
        probe.setActive(true);
        Example<TestPerson> example = Example.of(probe);
        Sort sort = Sort.by(Sort.Direction.ASC, "age");

        // When
        List<TestPerson> results = (List<TestPerson>) repository.findAll(example, sort);

        // Then
        assertThat(results).hasSizeGreaterThan(0);
        assertThat(results).allMatch(TestPerson::getActive);
        // Verify sorted
        for (int i = 0; i < results.size() - 1; i++) {
            assertThat(results.get(i).getAge())
                .isLessThanOrEqualTo(results.get(i + 1).getAge());
        }
    }

    @Test
    @DisplayName("Empty page request should return all results")
    void testFindAllUnpaged() {
        // When
        Page<TestPerson> page = repository.findAll(Pageable.unpaged());

        // Then
        assertThat(page.getContent()).hasSize(25);
        assertThat(page.getTotalElements()).isEqualTo(25L);
    }
}

