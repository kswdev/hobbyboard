package com.hobbyboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class AppTests {

    @Test
    void contextLoads() {

        String name = null;
        Optional<String> optional = Optional.ofNullable(name);
        optional.orElseThrow(() -> new IllegalArgumentException("zzz"));
    }

}
