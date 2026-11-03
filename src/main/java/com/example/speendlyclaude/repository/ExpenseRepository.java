package com.example.speendlyclaude.repository;

import com.example.speendlyclaude.model.Expenses;
import com.example.speendlyclaude.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expenses, Long> {
    List<Expenses> findByUserOrderByDateDesc(User user);
}
