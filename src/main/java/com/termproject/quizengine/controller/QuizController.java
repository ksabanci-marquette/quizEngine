package com.termproject.quizengine.controller;

import com.termproject.quizengine.dto.QuizDTO;
import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.Quiz;
import com.termproject.quizengine.model.QuizQuestion;
import com.termproject.quizengine.repository.QuestionRepository;
import com.termproject.quizengine.repository.QuizQuestionRepository;
import com.termproject.quizengine.repository.QuizRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final Logger log = LoggerFactory.getLogger(QuizController.class);


    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizQuestionRepository quizQuestionRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ModelMapper modelMapper;

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
        Quiz quiz = null;

        try {
            quiz =  quizRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Quiz","id",id));
            List<Long> questionIDList = quizQuestionRepository.findAllByQuizId(quiz.getId())
                                        .stream().map(QuizQuestion::getQuestionId).collect(Collectors.toList());

            List<Question> questionList = questionRepository.findAllByIdIn(questionIDList);
            QuizDTO quizDTO  = modelMapper.map(quiz, QuizDTO.class);
            quizDTO.setQuizQuestionList(questionList);

            return new ResponseEntity<>(quizDTO, HttpStatus.OK);
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
