package com.hutech.demo.repository;

import com.hutech.demo.model.User;
import com.hutech.demo.model.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    List<UserVoucher> findByUserOrderByCreatedAtDesc(User user);

    Optional<UserVoucher> findByCode(String code);
}
