package com.rock.coffeepickles.web;

import com.rock.coffeepickles.domain.Customer;
import com.rock.coffeepickles.repository.CustomerRepository;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rock.coffeepickles.web",
        "com.rock.coffeepickles.service"})
@EntityScan("com.rock.coffeepickles.domain")
@EnableJpaRepositories("com.rock.coffeepickles.repository")
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class CoffeepicklesApplication extends WebSecurityConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(CoffeepicklesApplication.class, args);
	}

	@Autowired
    private CustomerRepository customerRepository;

	@Override
    protected void configure(HttpSecurity http) throws Exception {
	    http.authorizeRequests().anyRequest().authenticated()
                .and()
                .x509()
                .subjectPrincipalRegex("CN=([^,]*)")
                .userDetailsService(userDetailsService());
    }

    @Bean
    protected UserDetailsService userDetailsService() {
	    return new UserDetailsService() {
	        @Override
            public UserDetails loadUserByUsername(String username) {
                Customer customer = customerRepository.findByUserName(username);
                if (customer == null) {
                    throw new UsernameNotFoundException(username);
                }
                return customer;
            }
        };
    }

	@Bean
	public KieContainer kieContainer() {
		return KieServices.Factory.get().getKieClasspathContainer();
	}
}
