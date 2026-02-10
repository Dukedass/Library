package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newBook)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.title").value("New Book"))
				.andExpect(jsonPath("$.author").value("Author B"))
				.andExpect(jsonPath("$.isbn").value("ISBN-0987654321"))
				.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	@DisplayName("POST /api/books - Validation Error")
	void createBook_ValidationError() throws Exception {
		Book invalidBook = new Book();
		invalidBook.setTitle(""); // Invalid: blank title
		invalidBook.setAuthor("A");
		invalidBook.setIsbn(""); // Invalid: blank ISBN

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidBook)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", containsString("Validation Failed")));
	}

	@Test
	@DisplayName("GET /api/books - List All")
	void getAllBooks_Success() throws Exception {
		mockMvc.perform(get("/api/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title").value("Sample Book"));
	}

	@Test
	@DisplayName("GET /api/books/{id} - Found")
	void getBookById_Found() throws Exception {
		Long id = testBook.getId();
		mockMvc.perform(get("/api/books/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.title").value("Sample Book"));
	}

	@Test
	@DisplayName("GET /api/books/{id} - Not Found")
	void getBookById_NotFound() throws Exception {
		mockMvc.perform(get("/api/books/{id}", 9999L))
				.andExpect(status().is5xxServerError())
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

		mockMvc.perform(put("/api/books/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Title"))
				.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	@DisplayName("PUT /api/books/{id} - Not Found")
	void updateBook_NotFound() throws Exception {
		Book updated = new Book();
		updated.setTitle("Updated Title");
		updated.setAuthor("Updated Author");
		updated.setIsbn("ISBN-0000000000");
		updated.setAvailable(false);

		mockMvc.perform(put("/api/books/{id}", 9999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updated)))
		.andExpect(status().is5xxServerError())
		.andExpect(jsonPath("$.error").value("Internal Server Error"));
	}

	@Test
	@DisplayName("DELETE /api/books/{id} - Success")
	void deleteBook_Success() throws Exception {
		Long id = testBook.getId();
		mockMvc.perform(delete("/api/books/{id}", id))
				.andExpect(status().isNoContent());
		Optional<Book> deleted = bookRepository.findById(id);
		assertFalse(deleted.isPresent());
	}

	@Test
	@DisplayName("DELETE /api/books/{id} - Not Found")
	void deleteBook_NotFound() throws Exception {
		mockMvc.perform(delete("/api/books/{id}", 9999L))
		.andExpect(status().is5xxServerError())
		.andExpect(jsonPath("$.error").value("Internal Server Error"));
	}
}
