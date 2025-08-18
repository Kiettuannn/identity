package com.kiettuan.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kiettuan.identity_service.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
