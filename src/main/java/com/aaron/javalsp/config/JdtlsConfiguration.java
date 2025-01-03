package com.aaron.javalsp.config;


import com.aaron.javalsp.CustomClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdtlsConfiguration {

    @Bean
    public CustomClient getCustomClient(){
        CustomClient customClient = new CustomClient();
        customClient.startup();
        return customClient;
    }
}
