package com.kiettuan.identity_service.mapper;

import org.mapstruct.Mapper;

import com.kiettuan.identity_service.dto.request.PermissionRequest;
import com.kiettuan.identity_service.dto.response.PermissionResponse;
import com.kiettuan.identity_service.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionsResponse(Permission permission);
}
