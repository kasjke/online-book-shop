package org.teamchallenge.bookshop.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {


    @PostConstruct
    public void setup() {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY");
    }
}