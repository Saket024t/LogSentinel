package com.logsentinel.gateway;

import com.logsentinel.gateway.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.logsentinel.gateway.security.CustomUserDetailsService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GatewayApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.flyway.enabled=false",
    "spring.data.elasticsearch.repositories.enabled=false",
    "spring.elasticsearch.uris=http://localhost:9200",
    "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "jwt.expiration=86400000"
})
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void unauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void viewerCanAccessLogsButNotAnomalies() throws Exception {
        UserDetails viewer = new User("viewer", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_VIEWER")));
        when(customUserDetailsService.loadUserByUsername("viewer")).thenReturn(viewer);

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(viewer, null, viewer.getAuthorities()));

        mockMvc.perform(get("/api/logs").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/anomalies").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminCanAccessEverything() throws Exception {
        UserDetails admin = new User("admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(admin);

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));

        mockMvc.perform(get("/api/logs").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/anomalies").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
