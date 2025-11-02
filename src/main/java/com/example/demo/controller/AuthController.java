package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public AuthController(UserRepository userRepository,PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,JwtUtil jwtUtil){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtUtil=jwtUtil;
    }
    @PostMapping("/register")
    public ResponseEntity<?>register(@RequestBody AuthRequest request){
        if(userRepository.existsByEmail(request.email())){
            return ResponseEntity.badRequest().body("Email already registered");
        }
        String encoded = passwordEncoder.encode(request.password());
        User user = new User(request.email(), encoded);
        userRepository.save(user);
        return ResponseEntity.ok("User Registered");
    }
    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody AuthRequest request){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
            String token = jwtUtil.generateToken(request.email());
            return ResponseEntity.ok(new AuthResponse(token));
        }catch(AuthenticationException e){
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }
}
