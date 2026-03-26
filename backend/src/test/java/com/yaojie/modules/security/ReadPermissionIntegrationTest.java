package com.yaojie.modules.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReadPermissionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "sales_clerk", authorities = {"SALES_CLERK"})
    void shouldForbidSalesClerkFromReadingPurchases() throws Exception {
        mockMvc.perform(get("/api/v1/purchases"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(4030))
            .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @WithMockUser(username = "sales_clerk", authorities = {"SALES_CLERK"})
    void shouldForbidSalesClerkFromReadingInventoryPageData() throws Exception {
        mockMvc.perform(get("/api/v1/inventories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(4030))
            .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @WithMockUser(username = "inventory_manager", authorities = {"INVENTORY_MANAGER"})
    void shouldForbidInventoryManagerFromReadingSales() throws Exception {
        mockMvc.perform(get("/api/v1/sales"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(4030))
            .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @WithMockUser(username = "sales_clerk", authorities = {"SALES_CLERK"})
    void shouldAllowSalesClerkToReadMedicineOptionsForSale() throws Exception {
        mockMvc.perform(get("/api/v1/medicines").param("status", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @WithMockUser(username = "sales_clerk", authorities = {"SALES_CLERK"})
    void shouldAllowSalesClerkToReadAvailableBatches() throws Exception {
        mockMvc.perform(get("/api/v1/inventories/available-batches").param("medicineId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }
}
