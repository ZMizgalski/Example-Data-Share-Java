package example.data.share.ExampleDataShareJava.controller;

import example.data.share.ExampleDataShareJava.model.role.ERole;
import example.data.share.ExampleDataShareJava.model.role.Role;
import example.data.share.ExampleDataShareJava.model.user.User;
import example.data.share.ExampleDataShareJava.payload.requests.login.LoginRequest;
import example.data.share.ExampleDataShareJava.payload.requests.registration.RegistrationRequest;
import example.data.share.ExampleDataShareJava.payload.responses.JWTResponse;
import example.data.share.ExampleDataShareJava.payload.responses.MessageResponse;
import example.data.share.ExampleDataShareJava.repositories.RoleRepo;
import example.data.share.ExampleDataShareJava.repositories.UserRepo;
import example.data.share.ExampleDataShareJava.security.jwt.JWTUtils;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JWTUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JWTResponse(jwt));
    }

    @SneakyThrows
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        if (userRepo.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }

        if (userRepo.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        User user = new User(registrationRequest.getUsername(), registrationRequest.getEmail(), encoder.encode(registrationRequest.getPassword()));

        Set<String> strRoles = registrationRequest.getRoles();
        Set<ERole> roles = new HashSet<>();

        if (strRoles == null) {
            Role role = roleRepo.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(role.getName());
        } else {
            for (String role : strRoles) {
                if ("admin".equals(role)) {
                    // throw new Exception("Rejestracja kont admina została wyłączona");
                    Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(adminRole.getName());
                } else {
                    Role userRole = roleRepo.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
                    roles.add(userRole.getName());
                }
            }
        }

        user.setRoles(roles);
        userRepo.save(user);

        return ResponseEntity.ok(new MessageResponse("User has been registered"));
    }
}
