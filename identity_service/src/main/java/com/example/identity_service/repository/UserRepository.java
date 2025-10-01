package com.example.identity_service.repository;

import com.example.identity_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = """
    select * from users u
    where (
        :q is null
        or u.username ilike '%' || :q || '%'
        or u.full_name ilike '%' || :q || '%'
        or u.email ilike '%' || :q || '%'
    )
    and (:status is null or u.status = :status)
    and u.deleted = false
    order by u.created_at desc
""", nativeQuery = true)
    Page<User> searchUsers(@Param("q") String q,
                           @Param("status") Short status,
                           Pageable pageable);

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByRoles_Id(String roleId);
    List<User> findAllByRoles_Id(String roleId);
}
