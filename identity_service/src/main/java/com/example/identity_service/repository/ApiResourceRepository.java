package com.example.identity_service.repository;

import com.example.identity_service.entity.ApiResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiResourceRepository extends JpaRepository<ApiResource, String> {
}
