package com.example.postreply;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
@MockBean(PostRepository.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class PostReplyApplicationTests {

    @Test
    void contextLoads() {
    }

}
