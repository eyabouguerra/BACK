package backAgil.example.back.controllers;

import backAgil.example.back.models.LoginRequest;
import backAgil.example.back.models.LoginResponse;
import backAgil.example.back.servicesImpl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@CrossOrigin("*")
@RestController
public class JwtController {
    @Autowired
    private JwtService jwtService;

    @PostMapping({"/authenticate"})
    public LoginResponse createJwtToken(@RequestBody LoginRequest loginRequest) throws Exception{
        return jwtService.createJwtToken(loginRequest);

    }
}