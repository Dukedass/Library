package com.example.librarymanagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.exception.BookNotFoundException;
import com.example.librarymanagement.repository.BookRepository;
import com.example.librarymanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private Book testBook;

	@MockBean
	private BookService bookService;

	@BeforeEach
	void setup() {
		bookRepository.deleteAll();
		testBook = new Book();
		testBook.setTitle("Sample Book");
		testBook.setAuthor("Author A");
		testBook.setIsbn("ISBN-1234567890");
		testBook.setAvailable(true);
		bookRepository.save(testBook);
	}

	@Test
	@DisplayName("POST /api/books - Success")
	void createBook_Success() throws Exception {

		Book newBook = new Book();
		newBook.setTitle("New Book");
		newBook.setAuthor("Author B");
		newBook.setIsbn("ISBN-0987654321");
		newBook.setAvailable(false);

		Book savedBook = new Book();
		savedBook.setId(1L);
		savedBook.setTitle("New Book");
		savedBook.setAuthor("Author B");
		savedBook.setIsbn("ISBN-0987654321");
		savedBook.setAvailable(false);

		when(bookService.createBook(any(Book.class))).thenReturn(savedBook);

		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newBook))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.title").value("New Book"))
				.andExpect(jsonPath("$.author").value("Author B"))
				.andExpect(jsonPath("$.isbn").value("ISBN-0987654321")).andExpect(jsonPath("$.available").value(false));
	}

	@Test
	@DisplayName("POST /api/books - Validation Error")
	void createBook_ValidationError() throws Exception {
		Book invalidBook = new Book();
		invalidBook.setTitle(""); // Invalid: blank title
		invalidBook.setAuthor("A");
		invalidBook.setIsbn(""); // Invalid: blank ISBN

		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidBook))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", containsString("Validation Failed")));
	}

	@Test
	@DisplayName("GET /api/books - List All")
	void getAllBooks_Success() throws Exception {

		Book book = new Book();
		book.setId(1L);
		book.setTitle("Sample Book");
		book.setAuthor("Author");
		book.setIsbn("ISBN-123");
		book.setAvailable(true);

		when(bookService.getAllBooks()).thenReturn(List.of(book));

		mockMvc.perform(get("/api/books")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title").value("Sample Book"))
				.andExpect(jsonPath("$[0].available").value(true));
	}

	@Test
	@DisplayName("GET /api/books/{id} - Found")
	void getBookById_Found() throws Exception {

		Long id = 1L;

		Book book = new Book();
		book.setId(id);
		book.setTitle("Sample Book");
		book.setAuthor("Author A");
		book.setIsbn("ISBN-123");
		book.setAvailable(true);

		when(bookService.getBookById(id)).thenReturn(Optional.of(book));

		mockMvc.perform(get("/api/books/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.title").value("Sample Book"));
	}

	@Test
	@DisplayName("GET /api/books/{id} - Not Found")
	void getBookById_NotFound() throws Exception {
		mockMvc.perform(get("/api/books/{id}", 9999L)).andExpect(status().is5xxServerError())
				.andExpect(jsonPath("$.error", containsString("Internal Server Error")));
	}

	@Test
	@DisplayName("PUT /api/books/{id} - Success")
	void updateBook_Success() throws Exception {
		Long id = testBook.getId();

		Book updated = new Book();
		updated.setTitle("Updated Title");
		updated.setAuthor("Updated Author");
		updated.setIsbn("ISBN-1234567890");
		updated.setAvailable(false);

		Book response = new Book();
		response.setId(id);
		response.setTitle("Updated Title");
		response.setAuthor("Updated Author");
		response.setIsbn("ISBN-1234567890");
		response.setAvailable(false);

		when(bookService.updateBook(eq(id), any(Book.class))).thenReturn(response);

		mockMvc.perform(put("/api/books/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updated))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Title")).andExpect(jsonPath("$.available").value(false));
	}

	@Test
	@DisplayName("PUT /api/books/{id} - Not Found")
	void updateBook_NotFound() throws Exception {
		Book updated = new Book();
		updated.setTitle("Updated Title");
		updated.setAuthor("Updated Author");
		updated.setIsbn("ISBN-0000000000");
		updated.setAvailable(false);

		mockMvc.perform(put("/api/books/{id}", 9999L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updated))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Resource Not Found"));
	}

	@Test
	void deleteBook_Success() throws Exception {
		Long id = 1L;

		doNothing().when(bookService).deleteBook(id);

		mockMvc.perform(delete("/api/books/{id}", id)).andExpect(status().isNoContent())
				.andExpect(content().string(""));

		verify(bookService, times(1)).deleteBook(id);
	}

	@Test
	@DisplayName("DELETE /api/books/{id} - Not Found")
	void deleteBook_NotFound() throws Exception {
		Long id = 999L;

		doThrow(new BookNotFoundException("Some internal message")).when(bookService).deleteBook(id);

		mockMvc.perform(delete("/api/books/{id}", id)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Resource Not Found"))
				.andExpect(jsonPath("$.message").value("Book not found with id: " + id))
				.andExpect(jsonPath("$.status").value(404));
	}

}
