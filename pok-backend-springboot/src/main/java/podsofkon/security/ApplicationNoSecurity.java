package podsofkon.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;


@Configuration
public class ApplicationNoSecurity {

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring()
//                    .antMatchers("/ai");
//                    .antMatchers("/ai")
                    .antMatchers("/**");

//todo remove this in order to reactivate basic auth once we figure out why POST calls
//give 401 "Full authentication is required to access this resource" while GET calls authenticate as expected
//is it jsession cookie setting or some such?
// note the same thing occurs with curl, eg curl -X POST http://localhost:8080/connect -u "admin:oraclespring"
        }
}
