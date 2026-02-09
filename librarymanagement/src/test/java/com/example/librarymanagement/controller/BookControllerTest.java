package com.example.librarymanagement.controller;

import com.example.librarymanagement.entity.Book;
import com.example.librarymanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	private Book testBook;

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		testBook = new Book();
		testBook.setId(1L);
		testBook.setTitle("Test Book");
		testBook.setAuthor("Test Author");
		testBook.setIsbn("1234567890");
	}

	@Test
    void createBook_Success() throws Exception {
        when(bookService.createBook(any(Book.class))).thenReturn(testBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

	@Test
	void getAllBooks_Success() throws Exception {
		Book book2 = new Book();
		book2.setId(2L);
		book2.setTitle("Book 2");
		when(bookService.getAllBooks()).thenReturn(List.of(testBook, book2));

		mockMvc.perform(get("/api/books")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Test Book")).andExpect(jsonPath("$[1].title").value("Book 2"));
	}

	@Test
    void getBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(testBook));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

	@Test
    void getBookById_NotFound() throws Exception {
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

	@Test
	void updateBook_Success() throws Exception {
		Book updatedBook = new Book();
		updatedBook.setTitle("Updated");
		updatedBook.setAuthor("New Author");
		updatedBook.setIsbn("1111111111");
		when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

		mockMvc.perform(put("/api/books/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedBook))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated"));
	}

	@Test
	void deleteBook_Success() throws Exception {
		mockMvc.perform(delete("/api/books/1")).andExpect(status().isNoContent());
	}
}
