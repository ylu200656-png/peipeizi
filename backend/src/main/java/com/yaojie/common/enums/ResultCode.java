package com.yaojie.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(0, "success"),
    BAD_REQUEST(4000, "Bad request"),
    VALIDATION_ERROR(4001, "Validation error"),
    LOGIN_FAILED(4002, "Username or password is incorrect"),
    UNAUTHORIZED(4010, "Unauthorized"),
    TOKEN_INVALID(4011, "Token is invalid"),
    FORBIDDEN(4030, "Forbidden"),
    NOT_FOUND(4040, "Not found"),
    MEDICINE_NOT_FOUND(4041, "Medicine not found"),
    USER_NOT_FOUND(4042, "User not found"),
    SUPPLIER_NOT_FOUND(4043, "Supplier not found"),
    INVENTORY_NOT_FOUND(4044, "Inventory not found"),
    USERNAME_EXISTS(4094, "Username already exists"),
    ROLE_ASSIGNMENT_INVALID(4093, "Role assignment is invalid"),
    MEDICINE_EXPIRED(4091, "Medicine batch is expired"),
    INVENTORY_NOT_ENOUGH(4092, "Inventory is not enough"),
    MEDICINE_CODE_EXISTS(4090, "Medicine code already exists"),
    INTERNAL_ERROR(5000, "Internal server error");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
