package com.termproject.quizengine.controller;

import com.termproject.quizengine.dto.QuizDTO;
import com.termproject.quizengine.dto.UserQuizStat2DTO;
import com.termproject.quizengine.dto.UserQuizStat3DTO;
import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.model.*;
import com.termproject.quizengine.payload.QuizSubmitPayload;
import com.termproject.quizengine.repository.*;
import com.termproject.quizengine.security.CurrentUser;
import com.termproject.quizengine.security.UserPrincipal;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.sql.JDBCType.NULL;


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

    @GetMapping("/stats/{id}")
    public ResponseEntity<?> getStats(@NotNull @PathVariable Long id) {

        try {
            return new ResponseEntity<>(quizRepository.findQuizStats(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats2/{id}")
    public ResponseEntity<?> getStats2(@NotNull @PathVariable Long id) {

        try {
            List<UserQuizStat2DTO> stats2 = quizRepository.findQuizStats2(id)
                    .stream().map(item -> new UserQuizStat2DTO(
                            item.get(0).toString(),
                            item.get(1).toString(),
                            item.get(2).toString()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(stats2, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats3/{id}")
    public ResponseEntity<?> getStats3(@NotNull @PathVariable Long id) {

        try {
            List<UserQuizStat3DTO> stats3 = quizRepository.findQuizStats3(id)
                    .stream().map(item -> new UserQuizStat3DTO(
                            item.get(0).toString(),
                            item.get(1).toString(),
                            item.get(2).toString(),
                            item.get(3)==null ? "NOT TAKEN" : item.get(3).toString()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(stats3, HttpStatus.OK);
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

    @Transactional
    @PostMapping("/save-with-questions")
    public ResponseEntity<?> saveWithQuestions(@RequestBody @Valid @NotNull QuizDTO quizDTO,
                                               @CurrentUser UserPrincipal userPrincipal) {
        log.debug("REST request to save ");
        try {
            Quiz quiz = new Quiz();
            quiz = Quiz.toQuiz(quiz, quizDTO);
            List<Question> quizQuestionList = quizDTO.getQuizQuestionList();
            List<Long> newQuestionIds = quizDTO.getQuizQuestionList().stream()
                    .map(Question::getId).collect(Collectors.toList());
            List<Long> currentQuestionIds = quizQuestionRepository.findAllByQuizId(quiz.getId())
                    .stream().map(QuizQuestion::getQuestionId).collect(Collectors.toList());

            List<Long> idsToRemove = currentQuestionIds.stream().filter(e -> !newQuestionIds.contains(e))
                    .collect(Collectors.toList());

            List<Long> idsToSave = newQuestionIds.stream().filter(e -> !currentQuestionIds.contains(e))
                    .collect(Collectors.toList());
            //
            quiz.setCreated_by(userPrincipal.getRecordId());
            quiz.setCreationDate(Date.from(Instant.now()));
            quiz = quizRepository.saveAndFlush(quiz);

            if(idsToRemove.size() != 0){
                quizQuestionRepository.deleteAllByQuestionIdIn(idsToRemove);
            }

            for(Long questionId: idsToSave){
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuizId(quiz.getId());
                quizQuestion.setQuestionId(questionId);
                quizQuestion = quizQuestionRepository.save(quizQuestion);
            }

            return ResponseEntity.ok().body("Success");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occured!");
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



        try {
            for (Question question : quizSubmitPayload.getQuizQuestionList()) {
                UserQuizQuestionAnswer userQuizQuestionAnswer= new UserQuizQuestionAnswer();
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
