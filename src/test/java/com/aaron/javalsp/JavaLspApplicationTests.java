package com.aaron.javalsp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JavaLspApplicationTests {


    @Autowired
    private CustomClient customClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void testOut(){
        customClient.startup();
        System.out.println("hello");
    }

}
