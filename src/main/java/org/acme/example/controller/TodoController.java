package org.acme.example.controller;

import org.acme.example.repository.TodoRepository;
import org.acme.example.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/todo")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private final TodoRepository repository;
    private final Map<Todo, String> tags;


    public TodoController(TodoRepository repository) {
        this.repository = repository;
        this.tags = new HashMap<>();
    }

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllTodoItems() {
        logger.debug("GET request access '/api/todo' path.");
        try {
            List<Todo> todos = repository.findAll();
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Nothing found", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addNewTodoItem(@RequestBody Todo item) {
        logger.debug("POST request access '/api/todo' path with item: {}", item);
        try {
            item.setId(UUID.randomUUID());
            repository.save(item);

            tags.put(item, item.getContent().toUpperCase()+"_created_on_"+ LocalDateTime.now());
            String message = String.format("Entity created and owner has tags %d %s", tags.size(), tags.values().stream()
                    .map(v -> v.toLowerCase() + "=" + v)
                    .collect(Collectors.joining(", ", "{", "}")));
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Entity creation failed", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTodoItem(@RequestBody Todo item) {
        logger.debug("PUT request access '/api/todo' path with item {}", item);
        try {
            Optional<Todo> todoItem = repository.findById(item.getId());
            if (todoItem.isPresent()) {
                repository.save(item);
                return new ResponseEntity<>("Entity updated", HttpStatus.OK);
            }
            return new ResponseEntity<>("Entity not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Edit errors: ", e);
            return new ResponseEntity<>("Update entity failed", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTodoItem(@PathVariable("id") UUID id) {
        logger.debug("DELETE request access '/api/todo/{}' path.", id);
        try {
            Optional<Todo> todoItem = repository.findById(id);
            if (todoItem.isPresent()) {
                repository.delete(todoItem.get());
                tags.remove(todoItem.get());
                String message = String.format("Entity deleted and owner has tags %d %s", tags.size(), tags.values().stream()
                        .map(v -> v.toLowerCase() + "=" + v)
                        .collect(Collectors.joining(", ", "{", "}")));
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            return new ResponseEntity<>("Not found the entity", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error occurred during delete: ", e);
            return new ResponseEntity<>("Deleting entity failed", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/simulate",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> simulateOverload() {
        logger.debug("Code that triggers an OOM");
        int count = 0;
        while(count < 30000) {
            Todo todo = new Todo();
            List<UUID> ids = repository.findAll().stream().map(t -> t.getId()).toList();
            count = ids.size();
            for (UUID id : ids) {
                todo.setId(id);
                todo.setContent("Another todo item " + todo.getContent()+id);
                this.updateTodoItem(todo);
            }
        }
        return new ResponseEntity<>("High load achieved", HttpStatus.OK);
    }
}
