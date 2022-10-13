package com.termproject.quizengine.controller;

import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.Quiz;
import com.termproject.quizengine.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final Logger log = LoggerFactory.getLogger(QuizController.class);


    @Autowired
    QuizRepository quizRepository;


    @GetMapping
    public ResponseEntity<?> getAll() {
        log.debug("REST request to get all ");
        try {
            return new ResponseEntity<>(quizRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById( @NotNull @PathVariable Long id) {
        log.debug("REST request to get by id ");
        try {
            return new ResponseEntity<>(quizRepository.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveOne(@RequestBody @Valid @NotNull Quiz quiz) {
        log.debug("REST request to get by id ");
        try {
            quizRepository.save(quiz);
            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occured!");
        }
    }


}
