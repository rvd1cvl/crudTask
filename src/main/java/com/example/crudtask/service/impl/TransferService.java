package com.example.crudtask.service.impl;

import com.example.crudtask.config.JwtUtil;
import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Transactional
    public String transferMoney(Long transferFromId, Long transferToId, BigDecimal value) {
        User transferFrom = userDAO.findById(transferFromId)
                .orElseThrow(() -> new IllegalArgumentException("Отправитель не найден"));
        if (!JwtUtil.getCurrentUsername().equals(transferFrom.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
        User transferTo = userDAO.findById(transferToId)
                .orElseThrow(() -> new IllegalArgumentException("Получатель не найден"));

        Account fromAccount = transferFrom.getAccount();
        Account toAccount = transferTo.getAccount();

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Один из пользователей не имеет аккаунта");
        }

        if (fromAccount.getBalance().compareTo(value) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для перевода");
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной");
        }

        synchronized (this) {
            fromAccount.setBalance(fromAccount.getBalance().subtract(value));

            toAccount.setBalance(toAccount.getBalance().add(value));

            accountDAO.save(fromAccount);
            accountDAO.save(toAccount);
        }

        return "Перевод успешен";
    }
}
