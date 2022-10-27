package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {


    Optional<User> findByEmailAddress(String email);

    Optional<User> findByEmailAddressAndActivated(String email,Boolean activated);

    Optional<User> findByUsernameOrEmailAddress(String username, String email);

    List<User> findByIdIn(List<Long> userRecordIds);

    Optional<User> findById(Long RecordId);

    User findFirstById(Long RecordId);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmailAddress(String email);

//    @Query(value = "select r.name from USER_ROLE ur left join ROLE_RIGHT rr on ur.role_id = rr.role_id join RIGHTS r on rr.right_id=r.id where ur.user_id = ?1", nativeQuery = true)
//    List<String> findAllUserRights(Long userId);

    Optional<User> findOneByResetKey(String resetKey);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET password=:password WHERE reset_key=:reset_key", nativeQuery = true)
    void updatePassword (@Param("reset_key") String reset_key, @Param("password")String password);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET password=:password WHERE id=:id", nativeQuery = true)
    void updatePasswordForId (@Param("id") Long id, @Param("password")String password);



    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET reset_key = NULL WHERE id = :id", nativeQuery = true)
    void deleteKeyForUser (@Param("id") Long id);


    @Query(value="SELECT * FROM user WHERE email_address= :email  AND id <> :id", nativeQuery = true)
    Optional<User> getByEmailAndRecordIdNotEquals(@Param("email") String email, @Param("id") Long id);

    @Query(value="SELECT * FROM user WHERE username= :username AND id <> :id", nativeQuery = true)
    Optional<User> getByUsernameAndRecordIdNotEquals(@Param("username") String username,  @Param("id") Long id);

}
