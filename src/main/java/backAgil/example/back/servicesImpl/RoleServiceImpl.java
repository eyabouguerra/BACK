package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Role;
import backAgil.example.back.repositories.RoleRepository;
import backAgil.example.back.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepo;
    public Role createNewRole(Role role){
     return   roleRepo.save(role);

    }
}
