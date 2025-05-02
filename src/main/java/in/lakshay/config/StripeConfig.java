package in.lakshay.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// stripe payment gateway config
@Configuration
public class StripeConfig {

    // get api key from env/properties
    @Value("${stripe.api.key}")
    private String stripeApiKey;  // secret key - keep this safe!

    // init stripe when app starts
    @PostConstruct
    public void init() {
        // set the global api key for all stripe calls
        Stripe.apiKey = stripeApiKey;
        // TODO: maybe add some logging here?
    }
}
