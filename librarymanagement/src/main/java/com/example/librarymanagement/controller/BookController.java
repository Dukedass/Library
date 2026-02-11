package com.example.librarymanagement.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.exception.BookNotFoundException;
import com.example.librarymanagement.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Library Management API", description = "Complete CRUD operations for Library Book Management System")
public class BookController {
	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@Operation(summary = "Create new book", description = "Add a new book to the library catalog")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input or ISBN already exists") })
	@PostMapping
	public ResponseEntity<Book> createBook(
			@Parameter(description = "Book object to create") @Valid @RequestBody Book book) {
		Book createdBook = bookService.createBook(book);
		return ResponseEntity.ok(createdBook);
	}

	@Operation(summary = "Get all books", description = "Retrieve complete list of books in library")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of books returned") })
	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks() {
		List<Book> books = bookService.getAllBooks();
		return ResponseEntity.ok(books);
	}

	@Operation(summary = "Get book by ID", description = "Fetch specific book details by its unique ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book found"),
			@ApiResponse(responseCode = "404", description = "Book not found") })
	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(
			@Parameter(description = "Book ID", required = true) @PathVariable Long id) {
		return bookService.getBookById(id)
				.map(ResponseEntity::ok)
				.orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));
	}

	@Operation(summary = "Update existing book", description = "Update book details by ID (title, author, ISBN, availability)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Book updated successfully"),
			@ApiResponse(responseCode = "404", description = "Book not found") })
	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(
			@Parameter(description = "Book ID to update", required = true) @PathVariable Long id,
			@Parameter(description = "Updated book details") @Valid @RequestBody Book bookDetails) {
		Book updatedBook = bookService.updateBook(id, bookDetails);
		if (updatedBook == null) {
			throw new BookNotFoundException("Book not found with id: " + id);
		}
		return ResponseEntity.ok(updatedBook);
	}

	@Operation(summary = "Delete book", description = "Remove a book from library catalog by ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Book not found") })
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(
			@Parameter(description = "Book ID to delete", required = true) @PathVariable Long id) {
		try {
			bookService.deleteBook(id);
		} catch (BookNotFoundException ex) {
			throw new BookNotFoundException("Book not found with id: " + id);
		}
		return ResponseEntity.noContent().build();
	}
}
