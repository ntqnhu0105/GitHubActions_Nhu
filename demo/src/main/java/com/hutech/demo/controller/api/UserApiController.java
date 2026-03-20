package com.hutech.demo.controller.api;

import com.hutech.demo.model.User;
import com.hutech.demo.repository.UserRepository;
import com.hutech.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> result = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users - Thêm người dùng mới
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String email = (String) body.get("email");
        String password = (String) body.get("password");

        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Tên và email là bắt buộc");
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Mật khẩu là bắt buộc khi thêm mới");
        }

        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(name) != null) {
            return ResponseEntity.badRequest().body("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(password);
        if (body.containsKey("phoneNumber") && body.get("phoneNumber") != null) {
            user.setPhone((String) body.get("phoneNumber"));
        }
        if (body.containsKey("dateOfBirth") && body.get("dateOfBirth") != null) {
            user.setDateOfBirth(LocalDate.parse((String) body.get("dateOfBirth")));
        }
        if (body.containsKey("address") && body.get("address") != null) {
            user.setAddress((String) body.get("address"));
        }

        userService.save(user); // mã hóa password và lưu

        // Gán role duy nhất theo yêu cầu
        String role = (String) body.get("role");
        if ("ADMIN".equalsIgnoreCase(role)) {
            userService.setExclusiveRole(name, com.hutech.demo.Role.ADMIN);
        } else if ("MANAGER".equalsIgnoreCase(role)) {
            userService.setExclusiveRole(name, com.hutech.demo.Role.MANAGER);
        } else {
            userService.setDefaultRole(name); // mặc định USER
        }

        User saved = userRepository.findByUsername(name);
        if (saved == null) saved = user;
        return ResponseEntity.ok(toDto(saved));
    }

    // PUT /api/users/{id} - Cập nhật người dùng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(existing -> {
            if (body.containsKey("name") && body.get("name") != null) {
                existing.setUsername((String) body.get("name"));
            }
            if (body.containsKey("email") && body.get("email") != null) {
                existing.setEmail((String) body.get("email"));
            }
            if (body.containsKey("phoneNumber") && body.get("phoneNumber") != null) {
                existing.setPhone((String) body.get("phoneNumber"));
            }
            if (body.containsKey("dateOfBirth") && body.get("dateOfBirth") != null) {
                String dob = (String) body.get("dateOfBirth");
                if (!dob.isBlank()) existing.setDateOfBirth(LocalDate.parse(dob));
            }
            if (body.containsKey("address") && body.get("address") != null) {
                existing.setAddress((String) body.get("address"));
            }
            // Chỉ cập nhật password nếu được gửi và không phải "defaultPassword"
            String newPassword = (String) body.get("password");
            if (newPassword != null && !newPassword.isBlank() && !newPassword.equals("defaultPassword")) {
                existing.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(newPassword));
            }
            // Cập nhật role nếu có gửi
            String newRole = (String) body.get("role");
            if (newRole != null && !newRole.isBlank()) {
                userRepository.save(existing); // save trước để username chắc chắn tồn tại
                if ("ADMIN".equalsIgnoreCase(newRole)) {
                    userService.setExclusiveRole(existing.getUsername(), com.hutech.demo.Role.ADMIN);
                } else if ("MANAGER".equalsIgnoreCase(newRole)) {
                    userService.setExclusiveRole(existing.getUsername(), com.hutech.demo.Role.MANAGER);
                } else {
                    userService.setExclusiveRole(existing.getUsername(), com.hutech.demo.Role.USER);
                }
            }
            userRepository.save(existing);
            return ResponseEntity.ok(toDto(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/users - Xóa tất cả người dùng
    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    // Map User entity -> DTO (frontend dùng: id, name, email, role, dateOfBirth, address, phoneNumber, createdAt)
    private Map<String, Object> toDto(User u) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", u.getId());
        dto.put("name", u.getUsername());
        dto.put("email", u.getEmail());
        dto.put("phoneNumber", u.getPhone());
        dto.put("address", u.getAddress());
        dto.put("dateOfBirth", u.getDateOfBirth() != null ? u.getDateOfBirth().toString() : null);
        dto.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "N/A");
        String role = u.getRoles().stream()
                .findFirst()
                .map(r -> r.getName())
                .orElse("USER");
        dto.put("role", role);
        return dto;
    }
}
