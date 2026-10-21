package io.github.whocares0604.spacedrepetition.review;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewConfiguration {

    @Bean
    public Sm2Scheduler sm2Scheduler() {
        return new Sm2Scheduler();
    }
}
