package com.df4j.xctec.xcms.system.repository;

import com.df4j.xctec.xcms.common.jpa.repository.BaseRepository;
import com.df4j.xctec.xcms.system.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserRepository extends BaseRepository<User> {

    java.util.Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "orgUnit"})
    java.util.Optional<User> findByTenantIdAndUsername(Long tenantId, String username);
}
