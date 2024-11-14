package com.ust.auth_service.controller;

import com.ust.auth_service.config.JwtTokenProvider;
import com.ust.auth_service.dto.*;
import com.ust.auth_service.model.Account;
import com.ust.auth_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtTokenProvider jwtService;


    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        try {
            String response = accountService.register(registerDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   // @PostMapping("/login")
    // public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
    //    return ResponseEntity.ok(accountService.login(loginDto));
  //  }
   @PostMapping("/login")
   public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
       Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
       );

       SecurityContextHolder.getContext().setAuthentication(authentication);

       // Fetch user details for token generation
       Optional<Account> existingUser = accountService.findByEmail(loginDto.getEmail());
       if (existingUser.isPresent()) {
           Account foundUser = existingUser.get();
           String jwt = jwtService.createToken(
                   foundUser.getEmail(),
                   //foundUser.getId(),
                   foundUser.getRoles()
           );

           // Create a new LoginResponse with token, id, and role
           LoginResponseDto response = new LoginResponseDto(jwt,foundUser.getRoles());
           return ResponseEntity.ok(response);
       } else {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
       }
   }

    @GetMapping("/validate/token")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(accountService.verify(token));
    }

    @GetMapping("/extract/roles")
    public ResponseEntity<Map<String, Object>> extractRolesFromToken(@RequestParam String token) {
        try {
            if (!accountService.verify(token)) {
                throw new RuntimeException("Invalid or Expired Token");
           }
            String roles = accountService.getRolesFromToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("roles", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            accountService.updatePassword(updatePasswordDto);
            return ResponseEntity.ok("Password updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/email")
    public ResponseEntity<String> updateEmail(@RequestBody UpdateEmailDto updateEmailDto) {
        try {
            accountService.updateEmail(updateEmailDto);
            return ResponseEntity.ok("Email updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
