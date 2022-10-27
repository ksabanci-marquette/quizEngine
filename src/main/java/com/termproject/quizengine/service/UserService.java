package com.termproject.quizengine.service;

import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.payload.ChangePasswordRequest;
import com.termproject.quizengine.payload.EmailPayload;
import com.termproject.quizengine.payload.UserSummary;
import com.termproject.quizengine.repository.UserRepository;
import com.termproject.quizengine.utils.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;


@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    MailService mailService;


    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User findOne(Long id) {
        log.debug("Request to get User : {}", id);
        return userRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User","ID",id));
    }


    //with key
    @Transactional
    public void updatePassword(ChangePasswordRequest dto) {
        log.debug("Request to updatePassword : {}", dto);
        try {
            String password;
            if (!StringUtils.isEmpty(dto.getPassword())  && !StringUtils.isEmpty(dto.getKey())){
                User user  = this.getByPasswordResetKey(dto.getKey().substring(1))
                                .orElseThrow(() -> new ResourceNotFoundException("User", "recordId",dto.getKey()));
                password = passwordEncoder.encode(dto.getPassword());
                userRepository.updatePassword(dto.getKey().substring(1), password);
                userRepository.deleteKeyForUser(user.getId());
            }
        }catch (Exception e){
            throw new RuntimeException("Change Password Failure!");
        }

    }

    @Transactional
    public void updatePasswordForMe(@NotNull Long recordId, @NotNull String password) {
        log.debug("Request to updatePassword : {}", recordId);

        try {
            if (!StringUtils.isEmpty(password))
                password = passwordEncoder.encode(password);
            userRepository.updatePasswordForId(recordId, password);

        }catch (Exception e){
            throw new RuntimeException("Change Password Failure!");
        }
    }


    @Transactional
    public String requestPasswordReset(EmailPayload dto) {
        log.debug("Request to reset mail : {}", dto.getEmail());

        return mailService.
                sendPasswordResetMail( userRepository.findByEmailAddressAndActivated(dto.getEmail(),true)
                        .map(user -> {
                            user.setResetKey(RandomUtil.generateResetKey());
                            user.setResetDate(Instant.now());
                            userRepository.saveAndFlush(user);
                            return user;
                        })
                        .orElseThrow(() -> new RuntimeException("User is Inactive, activate first!")));
    }


    public Optional<User> getByPasswordResetKey(String key){
        log.debug("Get user for reset key {}", key);
        return userRepository.findOneByResetKey(key);
    }

}
