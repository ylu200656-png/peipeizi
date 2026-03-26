package com.yaojie.common.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilTest {

    @Test
    void shouldMatchBcryptHash() {
        String encoded = PasswordUtil.encode("123456");

        assertThat(PasswordUtil.matches("123456", encoded)).isTrue();
    }

    @Test
    void shouldRejectPlaintextStoredPassword() {
        assertThat(PasswordUtil.matches("123456", "123456")).isFalse();
    }
}
