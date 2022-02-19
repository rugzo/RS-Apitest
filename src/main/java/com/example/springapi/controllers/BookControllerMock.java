package com.example.springapi.controllers;

import com.example.springapi.entity.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookControllerMock implements BookController {
    public static List<Book> booksList = new ArrayList<>();

    @Override
    @GetMapping()
    public List<Book> getBooks() {
        return booksList;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable int id) {
        var book = getBookById(id);

        if (book.isPresent()) {
            return new ResponseEntity<>(book.get(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>("book not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        var book = getBookById(id);
        if (book.isPresent()) {
            booksList.remove(book.get());
            return new ResponseEntity<>("book deleted", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("book to delete not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @PutMapping("")
    public ResponseEntity<?> addBook(@RequestBody Map<String, ?> requestBody) {
        // validation
        var reqId = requestBody.get("id");
        var reqAuthor = (String)requestBody.get("author");
        var reqTitle = (String)requestBody.get("title");
        if (reqAuthor == null || reqAuthor.isEmpty())  {
            return new ResponseEntity<>("Field 'author' is required", HttpStatus.BAD_REQUEST);
        }
        if (reqTitle == null || reqTitle.isEmpty())  {
            return new ResponseEntity<>("Field 'title' is required", HttpStatus.BAD_REQUEST);
        }
        if (reqId != null)  {
            return new ResponseEntity<>("Field 'id' is not allowed", HttpStatus.BAD_REQUEST);
        }

        // check already exists
        var checkBookExists= getBookByAuthorAndTitle(reqAuthor,
                                                                    reqTitle);
        if (checkBookExists.isPresent()) {
            return new ResponseEntity<>("book already exist with id: " + checkBookExists.get().getId(),
                    HttpStatus.BAD_REQUEST);
        }

        // if validated generateid and add book
        var bookToAdd = new Book(reqAuthor, reqTitle);
        bookToAdd.setId(generateBookId());
        booksList.add(bookToAdd);
        return new ResponseEntity<>(bookToAdd, HttpStatus.OK);
    }

    public Optional<Book> getBookById(int id) {
        return booksList.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    public Optional<Book> getBookByAuthorAndTitle(String author, String title) {
        return booksList.stream()
                .filter(book -> book.getAuthor().equals(author) && book.getTitle().equals(title))
                .findFirst();
    }

    public void ClearBookList() {
        booksList.clear();
    }

    static int id_counter;
    public int generateBookId() {
        return ++id_counter;
    }
}
