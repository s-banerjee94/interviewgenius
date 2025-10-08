package in.connectwithsandeepan.interviewgenius.userservice.repository;

import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(User.Role role, Pageable pageable);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    Page<User> findByIsVerified(Boolean isVerified, Pageable pageable);

    Page<User> findByRoleAndIsActive(User.Role role, Boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
           "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findUsersWithFilters(
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("email") String email,
        @Param("role") User.Role role,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate")
    Page<User> findRecentUsers(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    Page<User> findActiveUsers(Pageable pageable);

    Optional<User> findByEmailAndPassword(String email, String password);
}