package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.TokenUtil;
import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.factory.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingId, nonExistingId, dependentId;


    private Order order;
    private OrderDTO orderDTO;

    private User user;
    private Payment payment;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() throws Exception {
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";


        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 3L;

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        invalidToken = adminToken + "xpto"; // simulates wrong password

        user = new User(1L, "Bob", "bob@gmail.com", "249557684", LocalDate.of(1999, 11, 24), "123456");
        payment = new Payment(1L, Instant.now(), order);
        order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user, null);

        product = ProductFactory.createProduct();
        orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

    }

    @Test
    public void findByIdShouldReturnOrderDtoWhenLoggedAsAdmin() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        //aqui aferimos tudo que o postman retorna
        result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
        result.andExpect(jsonPath("$.status").value("PAID"));
        //a partir daqui, vemos somente se as listas existem
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
        result.andExpect(jsonPath("$.total").exists());
    }

    @Test
    public void findByIdReturnOrderWhenLoggedAsClientAndOrderBelongsToTheUser() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        //aqui aferimos tudo que o postman retorna
        result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
        result.andExpect(jsonPath("$.status").value("PAID"));
        //verificando se o cliente de fato é a Maria
        result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
        result.andExpect(jsonPath("$.payment").exists());
        result.andExpect(jsonPath("$.items").exists());
        result.andExpect(jsonPath("$.total").exists());
    }

    @Test
    public void findByIdReturnsForbiddenWhenOrderDoesnotBelongToTheUser() throws Exception {

        ResultActions result =
                // passamos uma ID de outro pedido que sabemos que pertence a outro usuário
                // dará forbidden pois o clientToken nesse teste, pertence à Maria Brown
                mockMvc.perform(get("/orders/{id}", 2L)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDontExistLoggedAsAdmin() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDontExistAndLoggedAsClient() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnUnauthorizedInvalidToken() throws Exception {
        ResultActions result =

                mockMvc.perform(get("/orders/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
}
