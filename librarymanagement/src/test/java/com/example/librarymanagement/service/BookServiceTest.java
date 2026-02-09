package com.example.librarymanagement.service;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookService bookService;

	private Book testBook;

	@BeforeEach
	void setUp() {
		testBook = new Book();
		testBook.setId(1L);
		testBook.setTitle("Test Book");
		testBook.setAuthor("Test Author");
		testBook.setIsbn("1234567890");
	}

	@Test
    void createBook_Success() {
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(testBook);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

	@Test
    void createBook_IsbnExists_ThrowsException() {
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(testBook));

        assertEquals("Book with ISBN 1234567890 already exists", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

	@Test
	void getAllBooks() {
		Book book2 = new Book();
		book2.setId(2L);
		book2.setTitle("Book 2");
		when(bookRepository.findAll()).thenReturn(List.of(testBook, book2));

		List<Book> result = bookService.getAllBooks();

		assertEquals(2, result.size());
		verify(bookRepository).findAll();
	}

	@Test
    void getBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }

	@Test
    void getBookById_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(999L);

        assertTrue(result.isEmpty());
    }

	@Test
	void updateBook_Success() {
		Book updatedBook = new Book();
		updatedBook.setTitle("Updated Title");
		updatedBook.setAuthor("Updated Author");
		updatedBook.setIsbn("0987654321");
		when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
		when(bookRepository.save(testBook)).thenReturn(testBook);

		Book result = bookService.updateBook(1L, updatedBook);

		assertEquals("Updated Title", result.getTitle());
		verify(bookRepository).save(testBook);
	}

	@Test
    void updateBook_NotFound_ThrowsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(999L, testBook));

        assertEquals("Book not found with id: 999", exception.getMessage());
    }

	@Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

	@Test
    void deleteBook_NotFound_ThrowsException() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook(999L));

        assertEquals("Book not found with id: 999", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
