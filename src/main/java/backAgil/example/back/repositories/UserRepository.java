package backAgil.example.back.repositories;

import backAgil.example.back.models.Role;
import backAgil.example.back.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,String> {

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")  // corrigé ici (roles au pluriel)
    List<User> findByRoleName(@Param("roleName") String roleName);
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);


}
