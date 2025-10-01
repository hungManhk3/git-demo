package com.example.identity_service.repository;

import com.example.identity_service.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByCode(String code);

    @Query(value = """
            select * from roles r
            where (
                :q is null
                or r.code ilike '%' || :q || '%'
                or r.name ilike '%' || :q || '%'
            )
            and r.deleted = false
            order by r.created_at desc
            
            """, nativeQuery = true)
    Page<Role> searchRoles(String q, Pageable pageable);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByCode(String code);

    boolean existsByPermissions_Id(String permissionId);
    List<Role> findAllByPermissions_Id(String permissionId);

}
