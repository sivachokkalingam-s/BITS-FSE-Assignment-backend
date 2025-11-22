package com.fse.equipment_management.controller;

import com.fse.equipment_management.data.AppUser;
import com.fse.equipment_management.repository.UserRepository;
import com.fse.equipment_management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AppUser appUser) {
        if (userRepo.findByUsername(appUser.getUsername()).isPresent())
            return ResponseEntity.badRequest().body("Username already exists");
        appUser.setPassword(encoder.encode(appUser.getPassword()));
        userRepo.save(appUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUser req, HttpServletResponse response) {
        Optional<AppUser> userOpt = userRepo.findByUsername(req.getUsername());
        if (userOpt.isEmpty())
//        !encoder.matches(req.getPassword(), userOpt.get().getPassword()))
            return ResponseEntity.status(403).body("Invalid credentials");

        AppUser appUser = userOpt.get();
        String token = jwtUtil.generateToken(appUser.getUsername(), appUser.getRole().name());
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        Map<String, Object> resp = new HashMap<>();
        resp.put("username", appUser.getUsername());
        resp.put("role", appUser.getRole().name());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/reauth")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = null;
        for (Cookie cookie : Optional.ofNullable(request.getCookies()).orElse(new Cookie[0])) {
            if ("auth_token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        return ResponseEntity.ok(Map.of("username", username, "role", role));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("logged out");
    }
}
