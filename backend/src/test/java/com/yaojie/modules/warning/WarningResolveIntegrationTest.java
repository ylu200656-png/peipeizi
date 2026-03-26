package com.yaojie.modules.warning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaojie.common.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WarningResolveIntegrationTest {

    private static final long ADMIN_USER_ID = 9001L;
    private static final long CLERK_USER_ID = 9002L;
    private static final long CATEGORY_ID = 5001L;
    private static final long SUPPLIER_ID = 6001L;
    private static final long MEDICINE_ID = 7001L;
    private static final long WARNING_ID = 8001L;

    private static final String ADMIN_USERNAME = "warning_admin";
    private static final String CLERK_USERNAME = "warning_clerk";
    private static final String ADMIN_NAME = "Warning Admin";
    private static final String CLERK_NAME = "Warning Clerk";
    private static final String CATEGORY_CODE = "TEST_CATEGORY";
    private static final String SUPPLIER_NAME = "Warning Supplier";
    private static final String MEDICINE_CODE = "MED-WARN-001";
    private static final String BATCH_NO = "WARN-BATCH-001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        applyWarningSchemaPatch();
        cleanTestData();
        insertTestData();
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldResolveWarningSuccessfully() throws Exception {
        mockMvc.perform(put("/api/v1/warnings/{id}/resolve", WARNING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("handleRemark", "manual checked"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.status").value("RESOLVED"))
            .andExpect(jsonPath("$.data.handledBy").value(ADMIN_USER_ID))
            .andExpect(jsonPath("$.data.handlerName").value(ADMIN_NAME))
            .andExpect(jsonPath("$.data.handleRemark").value("manual checked"));

        Map<String, Object> row = jdbcTemplate.queryForMap(
            "SELECT status, handled_by, handle_remark FROM warning_record WHERE id = ?",
            WARNING_ID
        );
        assertThat(row.get("status")).isEqualTo("RESOLVED");
        assertThat(((Number) row.get("handled_by")).longValue()).isEqualTo(ADMIN_USER_ID);
        assertThat(row.get("handle_remark")).isEqualTo("manual checked");
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldRejectResolvingResolvedWarning() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of("handleRemark", "repeat resolve"));

        mockMvc.perform(put("/api/v1/warnings/{id}/resolve", WARNING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(put("/api/v1/warnings/{id}/resolve", WARNING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(4095))
            .andExpect(jsonPath("$.message").value("Warning record is already resolved"));
    }

    @Test
    @WithMockUser(username = CLERK_USERNAME, authorities = {"SALES_CLERK"})
    void shouldRejectResolveWithoutPermission() throws Exception {
        mockMvc.perform(put("/api/v1/warnings/{id}/resolve", WARNING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("handleRemark", "forbidden"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(4030))
            .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, authorities = {"ADMIN"})
    void shouldReturnResolvedFieldsInList() throws Exception {
        mockMvc.perform(put("/api/v1/warnings/{id}/resolve", WARNING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("handleRemark", "list checked"))))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/warnings").param("status", "RESOLVED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data[0].id").value(WARNING_ID))
            .andExpect(jsonPath("$.data[0].status").value("RESOLVED"))
            .andExpect(jsonPath("$.data[0].handlerName").value(ADMIN_NAME))
            .andExpect(jsonPath("$.data[0].handleRemark").value("list checked"))
            .andExpect(jsonPath("$.data[0].handledAt").isNotEmpty());
    }

    private void applyWarningSchemaPatch() {
        Integer handledByColumn = jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warning_record' AND COLUMN_NAME = 'handled_by'",
            Integer.class
        );
        if (handledByColumn == null || handledByColumn == 0) {
            jdbcTemplate.execute("ALTER TABLE warning_record ADD COLUMN handled_by BIGINT UNSIGNED NULL");
        }

        Integer handleRemarkColumn = jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warning_record' AND COLUMN_NAME = 'handle_remark'",
            Integer.class
        );
        if (handleRemarkColumn == null || handleRemarkColumn == 0) {
            jdbcTemplate.execute("ALTER TABLE warning_record ADD COLUMN handle_remark VARCHAR(255) NULL");
        }
    }

    private void cleanTestData() {
        jdbcTemplate.update(
            "DELETE FROM operation_log WHERE user_id IN (?, ?) OR business_no = ?",
            ADMIN_USER_ID,
            CLERK_USER_ID,
            String.valueOf(WARNING_ID)
        );
        jdbcTemplate.update(
            "DELETE FROM warning_record WHERE id = ? OR medicine_id = ? OR handled_by IN (?, ?)",
            WARNING_ID,
            MEDICINE_ID,
            ADMIN_USER_ID,
            CLERK_USER_ID
        );
        jdbcTemplate.update(
            "DELETE FROM sys_user_role WHERE user_id IN (?, ?)",
            ADMIN_USER_ID,
            CLERK_USER_ID
        );
        jdbcTemplate.update(
            "DELETE FROM medicine WHERE id = ? OR medicine_code = ?",
            MEDICINE_ID,
            MEDICINE_CODE
        );
        jdbcTemplate.update(
            "DELETE FROM supplier WHERE id = ? OR supplier_name = ?",
            SUPPLIER_ID,
            SUPPLIER_NAME
        );
        jdbcTemplate.update(
            "DELETE FROM medicine_category WHERE id = ? OR category_code = ?",
            CATEGORY_ID,
            CATEGORY_CODE
        );
        jdbcTemplate.update(
            "DELETE FROM sys_user WHERE id IN (?, ?) OR username IN (?, ?)",
            ADMIN_USER_ID,
            CLERK_USER_ID,
            ADMIN_USERNAME,
            CLERK_USERNAME
        );
    }

    private void insertTestData() {
        jdbcTemplate.update(
            "INSERT INTO sys_user (id, username, password_hash, real_name, status) VALUES (?, ?, ?, ?, 1)",
            ADMIN_USER_ID,
            ADMIN_USERNAME,
            PasswordUtil.encode("123456"),
            ADMIN_NAME
        );
        jdbcTemplate.update(
            "INSERT INTO sys_user (id, username, password_hash, real_name, status) VALUES (?, ?, ?, ?, 1)",
            CLERK_USER_ID,
            CLERK_USERNAME,
            PasswordUtil.encode("123456"),
            CLERK_NAME
        );
        jdbcTemplate.update(
            "INSERT INTO sys_role (role_code, role_name) "
                + "SELECT 'ADMIN', 'System Admin' FROM DUAL "
                + "WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ADMIN')"
        );
        jdbcTemplate.update(
            "INSERT INTO sys_role (role_code, role_name) "
                + "SELECT 'SALES_CLERK', 'Sales Clerk' FROM DUAL "
                + "WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'SALES_CLERK')"
        );
        jdbcTemplate.update(
            "INSERT INTO sys_user_role (user_id, role_id) SELECT ?, id FROM sys_role WHERE role_code = 'ADMIN'",
            ADMIN_USER_ID
        );
        jdbcTemplate.update(
            "INSERT INTO sys_user_role (user_id, role_id) SELECT ?, id FROM sys_role WHERE role_code = 'SALES_CLERK'",
            CLERK_USER_ID
        );
        jdbcTemplate.update(
            "INSERT INTO medicine_category (id, category_name, category_code, remark) VALUES (?, ?, ?, ?)",
            CATEGORY_ID,
            "Warning Category",
            CATEGORY_CODE,
            "warning test"
        );
        jdbcTemplate.update(
            "INSERT INTO supplier (id, supplier_name, contact_person, phone, address, status) VALUES (?, ?, ?, ?, ?, 1)",
            SUPPLIER_ID,
            SUPPLIER_NAME,
            "Tester",
            "13800000000",
            "Warning Address"
        );
        jdbcTemplate.update(
            "INSERT INTO medicine "
                + "(id, medicine_code, medicine_name, category_id, specification, unit, manufacturer, supplier_id, "
                + "purchase_price, sale_price, safe_stock, expiry_warning_days, is_controlled, status, remark) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 10.00, 12.00, 5, 30, 0, 1, ?)",
            MEDICINE_ID,
            MEDICINE_CODE,
            "Warning Test Medicine",
            CATEGORY_ID,
            "10mg*10",
            "box",
            "Warning Factory",
            SUPPLIER_ID,
            "warning test"
        );
        jdbcTemplate.update(
            "INSERT INTO warning_record "
                + "(id, medicine_id, batch_no, warning_type, warning_level, warning_message, status, created_at, "
                + "handled_at, handled_by, handle_remark) "
                + "VALUES (?, ?, ?, 'LOW_STOCK', 'WARN', 'Warning integration test', 'OPEN', NOW(), NULL, NULL, NULL)",
            WARNING_ID,
            MEDICINE_ID,
            BATCH_NO
        );
    }
}
