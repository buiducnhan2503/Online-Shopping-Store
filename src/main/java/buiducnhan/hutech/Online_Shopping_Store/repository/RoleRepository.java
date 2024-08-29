package buiducnhan.hutech.Online_Shopping_Store.repository;

import buiducnhan.hutech.Online_Shopping_Store.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);
}

