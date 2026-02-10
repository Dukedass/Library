package com.example.librarymanagement.service;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public Book createBook(Book book) {
		if (bookRepository.existsByIsbn(book.getIsbn())) {
			throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
		}
		return bookRepository.save(book);
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Optional<Book> getBookById(Long id) {
		return bookRepository.findById(id);
	}

	public Book updateBook(Long id, Book bookDetails) {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
		// For optimistic locking, set the version from the incoming details
		book.setTitle(bookDetails.getTitle());
		book.setAuthor(bookDetails.getAuthor());
		book.setIsbn(bookDetails.getIsbn());
		book.setAvailable(bookDetails.isAvailable());
		book.setVersion(bookDetails.getVersion()); // Ensure version is set for optimistic locking

		try {
			return bookRepository.save(book);
		} catch (OptimisticLockingFailureException | OptimisticLockException e) {
			throw new OptimisticLockException("Concurrent update detected for book with id: " + id);
		}
	}

	public void deleteBook(Long id) {
		if (!bookRepository.existsById(id)) {
			throw new IllegalArgumentException("Book not found with id: " + id);
		}
		bookRepository.deleteById(id);
	}
}
