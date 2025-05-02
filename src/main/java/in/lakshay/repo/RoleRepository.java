package in.lakshay.repo;

import in.lakshay.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

// super simple repo for user roles
// just basic CRUD + find by name
public interface RoleRepository extends JpaRepository<Role, Long> {
    // find role by name (ROLE_USER, ROLE_ADMIN)
    Role findByName(String name); // used during registration
}