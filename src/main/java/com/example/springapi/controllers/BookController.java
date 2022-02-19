package com.example.springapi.controllers;

import com.example.springapi.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface BookController {
    @GetMapping()
    List<Book> getBooks();

    @GetMapping("/{id}")
    ResponseEntity<?> getBook(@PathVariable int id);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteBook(@PathVariable int id);

    @PutMapping("")
    ResponseEntity<?> addBook(@RequestBody Map<String, ?> requestBody);
}
