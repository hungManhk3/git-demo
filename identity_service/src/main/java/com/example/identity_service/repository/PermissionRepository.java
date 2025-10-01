package com.example.identity_service.repository;

import com.example.identity_service.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsByCode(String code);

    @Query(value = """
            select * from permissions p
            where (
                :q is null
                or p.code ilike '%' || :q || '%'
                or p.resource ilike '%' || :q || '%'
                or p.action ilike '%' || :q || '%'
            )
            and p.deleted = false
            order by p.created_at desc
            
            """, nativeQuery = true)
    Page<Permission> searchPermissions(String q, Pageable pageable);
    Optional<Permission> findByCode(String code);
}
