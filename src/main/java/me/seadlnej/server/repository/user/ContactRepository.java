package me.seadlnej.server.repository.user;

import me.seadlnej.server.model.UserContact;
import me.seadlnej.server.model.UserContactId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<UserContact, UserContactId> {

    List<UserContact> findByUserId(Long userId);

    @Query("SELECT uc FROM UserContact uc WHERE uc.userId = :userId")
    List<UserContact> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT uc FROM UserContact as uc WHERE uc.userId = :userId OR uc.contactId = :userId")
    List<UserContact> findAllConnections(@Param("userId") Long userId);

    @Query("SELECT uc FROM UserContact uc WHERE uc.userId = :userId AND uc.contactId = :contactId")
    UserContact findByUserIdAndContactId(@Param("userId") Long userId, @Param("contactId") Long contactId);

    // ------------------------------------------------------------
    //               NEW FIXED METHODS (WORKING)
    // ------------------------------------------------------------

    @Query("""
        SELECT COUNT(uc) > 0 FROM UserContact uc
        WHERE (uc.userId = :userId AND uc.contactId = :contactId)
           OR (uc.userId = :contactId AND uc.contactId = :userId)
    """)
    boolean areFriends(@Param("userId") Long userId,
                       @Param("contactId") Long contactId);


    // ✅ Check for existing friendship request (any direction)
    @Query("""
        SELECT COUNT(r) > 0 FROM UserRequest r
        WHERE (r.senderId = :userId AND r.receiverId = :contactId)
           OR (r.senderId = :contactId AND r.receiverId = :userId)
          AND r.status = 'pending'
    """)
    boolean requestExists(@Param("userId") Long userId,
                          @Param("contactId") Long contactId);

}