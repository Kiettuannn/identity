package com.kiettuan.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kiettuan.identity_service.dto.request.RoleRequest;
import com.kiettuan.identity_service.dto.response.RoleResponse;
import com.kiettuan.identity_service.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    // ignore permissions to convert from Set<String> of request to Set<Permission> of RoleResponse (like Entity)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
