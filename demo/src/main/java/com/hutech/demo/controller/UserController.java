package com.hutech.demo.controller;

import com.hutech.demo.model.User;
import com.hutech.demo.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
 private final UserService userService;
 @GetMapping("/login")
 public String login() {
 return "users/login";
 }
 @GetMapping("/register")
 public String register(@NotNull Model model) {
 model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
 return "users/register";
 }
 @PostMapping("/register")
 public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
 @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
 Model model) {
 if (bindingResult.hasErrors()) {
 var errors = bindingResult.getAllErrors()
 .stream()
 .map(DefaultMessageSourceResolvable::getDefaultMessage)
 .toArray(String[]::new);
 model.addAttribute("errors", errors);
 return "users/register";
 }
 try {
 userService.save(user);
 userService.setDefaultRole(user.getUsername());
 return "redirect:/login";
 } catch (DataIntegrityViolationException e) {
 List<String> errors = new ArrayList<>();
 String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
 if (msg.contains("username")) {
 errors.add("Tên đăng nhập đã tồn tại.");
 }
 if (msg.contains("email")) {
 errors.add("Email đã được sử dụng.");
 }
 if (msg.contains("phone")) {
 errors.add("Số điện thoại đã được đăng ký.");
 }
 if (errors.isEmpty()) {
 errors.add("Thông tin đăng ký trùng với tài khoản đã có (tên đăng nhập, email hoặc số điện thoại). Vui lòng thử lại.");
 }
 model.addAttribute("errors", errors);
 return "users/register";
 }
 }
}