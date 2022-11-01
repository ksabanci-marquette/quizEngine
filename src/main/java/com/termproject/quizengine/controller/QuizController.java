package com.termproject.quizengine.controller;

import com.termproject.quizengine.dto.QuizDTO;
import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.model.*;
import com.termproject.quizengine.payload.QuizSubmitPayload;
import com.termproject.quizengine.repository.*;
import com.termproject.quizengine.security.CurrentUser;
import com.termproject.quizengine.security.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQuizQuestionAnswerRepository userQuizQuestionAnswerRepository;

    @Autowired
    UserQuizGradeRepository userQuizGradeRepository;


    @GetMapping
    public ResponseEntity<?> getAll() {
        log.debug("REST request to get all ");
        try {
            return new ResponseEntity<>(quizRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAllAvailable(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("REST request to get all ");
        try {
            return new ResponseEntity<>(quizRepository.findQuizzesForUser(userPrincipal.getRecordId()), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest(@CurrentUser UserPrincipal userPrincipal) {
        log.debug("REST request to get all ");
        try {
            if (userPrincipal.getIsAdmin())
                return new ResponseEntity<>(quizRepository.findAllQuizResults(), HttpStatus.OK);
            else
                return new ResponseEntity<>(quizRepository.findLatestQuizResults(userPrincipal.getRecordId()), HttpStatus.OK);
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
        log.debug("REST request to save ");
        try {
            quizRepository.save(quiz);
            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occured!");
        }
    }

    @Transactional
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody @Valid @NotNull QuizSubmitPayload quizSubmitPayload,
                                    @CurrentUser UserPrincipal userPrincipal) {
        log.debug("REST request to submit ");
        int counter=0;
        int numberOfQuestions=quizSubmitPayload.getQuizQuestionList().size();
        int totalCorrectAnswers=0;
        double grade= 0.0d;

        if( quizSubmitPayload.getUserAnswers().size()==0)
            return ResponseEntity.badRequest().body("Improper data!");

        User user = userRepository.findById(userPrincipal.getRecordId()).orElseThrow(
                () -> new ResourceNotFoundException("User","id",userPrincipal.getRecordId()));

        Integer currentAttempt = userQuizGradeRepository.
                findMaxAttemptbyQuizandUser(userPrincipal.getRecordId(),quizSubmitPayload.getQuizId()).
                orElse(0) + 1;

        UserQuizQuestionAnswer userQuizQuestionAnswer= new UserQuizQuestionAnswer();

        try {
            for (Question question : quizSubmitPayload.getQuizQuestionList()) {
                userQuizQuestionAnswer.setUserId(user.getId());
                userQuizQuestionAnswer.setQuizId(quizSubmitPayload.getQuizId());
                userQuizQuestionAnswer.setQuestionId(question.getId());
                userQuizQuestionAnswer.setRealAnswer(question.getQuestionAnswers().
                        stream().filter(answer -> answer.isAnswer()).collect(Collectors.toList()).get(0).getAnswerDescription());
                userQuizQuestionAnswer.setUserAnswer(quizSubmitPayload.getUserAnswers().get(counter)== null
                        ? null :
                        question.getQuestionAnswers().get(quizSubmitPayload.getUserAnswers().get(counter)).getAnswerDescription());

                if (userQuizQuestionAnswer.getUserAnswer()!=null && userQuizQuestionAnswer.getRealAnswer()!=null){
                    if(userQuizQuestionAnswer.getUserAnswer().equals(userQuizQuestionAnswer.getRealAnswer())){
                        totalCorrectAnswers = totalCorrectAnswers+1;
                    }}

                userQuizQuestionAnswer.setCurrentAttempt(currentAttempt);
                userQuizQuestionAnswer.setCreationDate(Date.from(Instant.now()));
                //save
                userQuizQuestionAnswerRepository.save(userQuizQuestionAnswer);
                counter++;
            }

            //set grade
            grade = (double) totalCorrectAnswers / numberOfQuestions * 100 ;
            UserQuizGrade userQuizGrade = new UserQuizGrade();
            userQuizGrade.setQuizId(quizSubmitPayload.getQuizId());
            userQuizGrade.setUserId(userPrincipal.getRecordId());
            userQuizGrade.setGrade(grade);
            userQuizGrade.setLastAttempt(currentAttempt);
            userQuizGrade.setCreationDate(Date.from(Instant.now()));
            userQuizGradeRepository.save(userQuizGrade);

            return ResponseEntity.ok().body("Your Answers saved successfully, Your Grade is: "+grade);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
