package com.hutech.demo;
import com.hutech.demo.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration // Đánh dấu lớp này là một lớp cấu hình cho Spring Context.
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security.
@RequiredArgsConstructor // Lombok tự động tạo constructor có tham số cho tất cả các trường final.
public class SecurityConfig {
 private final UserService userService; // Tiêm UserService vào lớp cấu hình này.
 @Bean // Đánh dấu phương thức trả về một bean được quản lý bởi Spring Context.
 public UserDetailsService userDetailsService() {
 return new UserService(); // Cung cấp dịch vụ xử lý chi tiết người dùng.
 }
 @Bean
 public PasswordEncoder passwordEncoder() {
 return new BCryptPasswordEncoder(); // Bean mã hóa mật khẩu sử dụng BCrypt.
 }
 @Bean
 public DaoAuthenticationProvider authenticationProvider() {
 var auth = new DaoAuthenticationProvider(); // Tạo nhà cung cấp xác thực.
 auth.setUserDetailsService(userDetailsService()); // Thiết lập dịch vụ chi tiết người dùng.
 auth.setPasswordEncoder(passwordEncoder()); // Thiết lập cơ chế mã hóa mật khẩu.
 return auth; // Trả về nhà cung cấp xác thực.
 }
 @Bean
 public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
 return http
 .csrf(csrf -> csrf
     .ignoringRequestMatchers("/api/**") // AJAX không gửi CSRF token
 )
 .authorizeHttpRequests(auth -> auth
 .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/login", "/oauth/**", "/register", "/error")
 .permitAll()
 .requestMatchers("/products", "/products/detail/**", "/cart", "/cart/**")
 .permitAll() // USER và khách: xem sản phẩm, mua hàng.
 .requestMatchers("/products/add", "/products/edit/**", "/products/update/**", "/products/delete/**")
 .hasAnyAuthority("ADMIN", "MANAGER") // ADMIN và MANAGER: thêm, sửa, xóa sản phẩm.
 .requestMatchers("/categories", "/categories/**")
 .hasAnyAuthority("ADMIN") // Chỉ ADMIN: quản lý danh mục.
 .requestMatchers("/user/points", "/user/points/**")
 .hasAnyAuthority("USER") // Chỉ USER: trang Điểm tích lũy & đổi voucher.
 .requestMatchers("/api/**").permitAll()
 .requestMatchers("/qls/**").permitAll() // Trang quản lý AJAX
 .anyRequest().authenticated()
 )
 .logout(logout -> logout
 .logoutUrl("/logout")
 .logoutSuccessUrl("/") // Về trang chủ sau khi đăng xuất (hiển thị lại nút Đăng nhập).
 .deleteCookies("JSESSIONID") // Xóa cookie.
 .invalidateHttpSession(true) // Hủy phiên làm việc.
 .clearAuthentication(true) // Xóa xác thực.
 .permitAll()
 )
 .formLogin(formLogin -> formLogin
 .loginPage("/login") // Trang đăng nhập.
 .loginProcessingUrl("/login") // URL xử lý đăng nhập.
 .defaultSuccessUrl("/") // Trang sau đăng nhập thành công.
 .failureUrl("/login?error") // Trang đăng nhập thất bại.
 .permitAll()
 )
 .rememberMe(rememberMe -> rememberMe
 .key("hutech")
 .rememberMeCookieName("hutech")
 .tokenValiditySeconds(24 * 60 * 60) // Thời gian nhớ đăng nhập.
 .userDetailsService(userDetailsService())
 )
 .exceptionHandling(exceptionHandling -> exceptionHandling
 .accessDeniedPage("/403") // Trang báo lỗi khi truy cập không được phép.
 )
 .sessionManagement(sessionManagement -> sessionManagement
 .maximumSessions(1) // Giới hạn số phiên đăng nhập.
 .expiredUrl("/login") // Trang khi phiên hết hạn.
 )
 .httpBasic(httpBasic -> httpBasic
 .realmName("hutech") // Tên miền cho xác thực cơ bản.
 )
 .build(); // Xây dựng và trả về chuỗi lọc bảo mật.
 }
}

