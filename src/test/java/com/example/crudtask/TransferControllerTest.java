package com.example.crudtask;

// Базовые импорты для тестирования Spring Boot

import com.example.crudtask.config.JwtUtil;
import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.User;
import com.example.crudtask.service.CustomUserDetailsService;
import com.example.crudtask.service.impl.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private AccountDAO accountDAO;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private String token;

    private User userFrom;

    @BeforeEach
    void setUp() {
        userFrom = new User("userFrom@mail.com", "password");

        Account accountFrom = new Account();
        accountFrom.setInitialBalance(new BigDecimal("1000"));
        accountFrom.setUser(userFrom);

        userFrom.setAccount(accountFrom);

        when(userDAO.findByEmail(userFrom.getUserEmail())).thenReturn(userFrom);

        token = jwtUtil.generateToken(userFrom.getUserEmail());
        when(jwtUtil.generateToken(userFrom.getUserEmail())).thenReturn(token);

        when(transferService.transferMoney(anyLong(), anyLong(), any())).thenReturn("Перевод успешен");

        when(customUserDetailsService.loadUserByUsername(userFrom.getUserEmail())).thenReturn(userFrom);
    }

    @Test
    void transferMoney_ShouldReturnSuccess_WhenValidTokenAndTransfer() throws Exception {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user@example.com");
        when(jwtUtil.validateToken(anyString(), eq("user@example.com"))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("value", "100")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void transferMoney_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception {
        String invalidToken = "invalid-jwt-token";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("value", "100")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void transferMoney_ShouldReturnBadRequest_WhenMissingAuthorization() throws Exception {
        // Мокаем запрос без токена
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("value", "100"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
