package com.termproject.quizengine.controller;

import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    Environment environment;


    @Autowired
    QuestionRepository questionRepository;


    @GetMapping
    public ResponseEntity<?> getAll() {
        log.debug("REST request to get all ");
        try {
            return new ResponseEntity<>(questionRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById( @NotNull @PathVariable Long id) {
        log.debug("REST request to get by id ");
        try {
            return new ResponseEntity<>(questionRepository.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveOne(@RequestBody @Valid @NotNull Question question) {
        log.debug("REST request to get by id ");
        try {
            questionRepository.save(question);
            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occured!");
        }
    }


}
