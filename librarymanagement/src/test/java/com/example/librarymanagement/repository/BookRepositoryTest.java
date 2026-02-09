package com.example.librarymanagement.repository;

import com.example.librarymanagement.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

	@Autowired
	private BookRepository bookRepository;

	@Test
	@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	void testFindByIsbn() {
		Optional<Book> book = bookRepository.findByIsbn("1234567890");
		assertTrue(book.isPresent());
		assertEquals("Test Book", book.get().getTitle());
	}

	@Test
	void testCreateAndFindBook() {
		Book book = new Book();
		book.setTitle("New Book");
		book.setAuthor("New Author");
		book.setIsbn("0987654321");
		Book saved = bookRepository.save(book);

		assertNotNull(saved.getId());
		Optional<Book> found = bookRepository.findById(saved.getId());
		assertTrue(found.isPresent());
		assertEquals("New Book", found.get().getTitle());
	}

	@Test
	void testExistsByIsbn() {
		Book book = new Book();
		book.setTitle("New Book");
		book.setAuthor("New Author");
		book.setIsbn("1111111111");
		bookRepository.save(book);
		assertTrue(bookRepository.existsByIsbn("1111111111"));
		assertFalse(bookRepository.existsByIsbn("9999999999"));
	}

}
