package com.example.springapi;

import com.example.springapi.controllers.BookControllerMock;
import com.example.springapi.entity.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BookControllerMock.class)
public class BookControllerIntTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookControllerMock bookController;

    @Before
    public void clearBooks() {
        // before every test reset temp book list of mock controllers
        bookController.ClearBookList();
    }

    @Test
    public void whenAppJustStarted_BooksRouteShouldReturnEmptyResponse() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    public void whenNotExistingBookRequested_shouldReturnNotFoundError() throws Exception {
        mvc.perform(get("/api/book/99"))
                 .andExpect(status().is(404));
    }

    @Test
    public void whenAddingABook_givenAuthorIsEmpty_shouldReturnBadRequestError() throws Exception {
        var bookToAdd = new HashMap<>();
        bookToAdd.put("title", "book title");
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        mvc.perform(put("/api/books")
                        .content(bookJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("'author' is required")));

        mvc.perform(get("/api/books"))
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    public void whenAddingABook_givenTitleIsEmpty_shouldReturnBadRequestError() throws Exception {
        var bookToAdd = new HashMap<>();
        bookToAdd.put("author", "author name");
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        mvc.perform(put("/api/books")
                        .content(bookJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string(containsString("'title' is required")));

        mvc.perform(get("/api/books"))
                .andExpect(jsonPath("$", is(empty())));
    }

    public void whenAddingABook_givenBookAlreadyExists_ShouldReturnBadRequestError() throws Exception {
        var bookToAdd = new HashMap<>();
        bookToAdd.put("title", "book title");
        bookToAdd.put("author", "author name");
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        // when adding a new valid book, api should return 200
        mvc.perform(put("/api/books")
                        .content(bookJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        // when adding a book with existing author name and title, api should return 400
        mvc.perform(put("/api/books")
                        .content(bookJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void whenAddingABook_givenIdIsSet_shouldReturnBadRequestError() throws Exception {
        var bookToAdd = new HashMap<>();
        bookToAdd.put("id", 1);
        bookToAdd.put("title", "book title");
        bookToAdd.put("author", "author name");
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        mvc.perform(put("/api/books")
                        .content(bookJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void whenABookAdded_shouldReturnAddedBook() throws Exception {
        var bookTitle = "book title";
        var bookAuthor = "author name";
        var bookToAdd = new HashMap<>();
        bookToAdd.put("title", bookTitle);
        bookToAdd.put("author", bookAuthor);
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        mvc.perform(put("/api/books")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['author']", is(bookAuthor)))
                .andExpect(jsonPath("$['title']", is(bookTitle)));
    }

    @Test
    public void whenABookAdded_shouldBeReturnedWithId() throws Exception {
        var bookTitle = "book title";
        var bookAuthor = "author name";
        var bookToAdd = new HashMap<>();
        bookToAdd.put("title", bookTitle);
        bookToAdd.put("author", bookAuthor);
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        var addBookResponse = mvc.perform(put("/api/books")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var addedBook = objectMapper.readValue(addBookResponse, Book.class);

        mvc.perform(get("/api/books/" + addedBook.getId()))
                .andExpect(jsonPath("$['author']", is(bookAuthor)))
                .andExpect(jsonPath("$['title']", is(bookTitle)));
    }

    @Test
    public void whenABookAdded_shouldBooksRouteMustIncludeSameBook() throws Exception {
        var bookToAdd = new HashMap<>();
        bookToAdd.put("title", "book title");
        bookToAdd.put("author", "author name");
        var bookJson = objectMapper.writeValueAsString(bookToAdd);

        var addBookResponse = mvc.perform(put("/api/books")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var addedBook = objectMapper.readValue(addBookResponse, Book.class);

        mvc.perform(get("/api/books"))
                .andExpect(jsonPath("$[-1]['id']", is(addedBook.getId())));
    }
}