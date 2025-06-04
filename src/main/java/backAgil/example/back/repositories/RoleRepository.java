package backAgil.example.back.repositories;

import backAgil.example.back.models.Role;
import backAgil.example.back.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role,String> {
    Optional<Role> findByRoleName(String roleName);



}
