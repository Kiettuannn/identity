package com.kiettuan.identity_service.mapper;

import com.kiettuan.identity_service.dto.request.PermissionRequest;
import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.request.UserUpdateRequest;
import com.kiettuan.identity_service.dto.response.PermissionResponse;
import com.kiettuan.identity_service.dto.response.UserResponse;
import com.kiettuan.identity_service.entity.Permission;
import com.kiettuan.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionsResponse(Permission permission);
}
