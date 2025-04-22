package lk.udcreations.product.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.config.UserServiceClient;
import lk.udcreations.product.security.AuthUtils;

@TestConfiguration
@Profile("test")
@EnableFeignClients
public class TestConfig {

    @Bean
    @Primary
    public UserServiceClient userServiceClient() {
        return new UserServiceClient() {
            @Override
            public UsersDTO getUserDetails(String username) {
                UsersDTO user = new UsersDTO();
                user.setUserId(1);
                user.setUsername(username);
                return user;
            }

            @Override
            public UsersDTO getUserById(Integer userId) {
                UsersDTO user = new UsersDTO();
                user.setUserId(userId);
                user.setUsername("test_user");
                return user;
            }
        };
    }

    @Bean
    @Primary
    public AuthUtils authUtils() {
        return new AuthUtils(userServiceClient()) {
            @Override
            public String getLoggedInUsername() {
                return "test_user";
            }

            @Override
            public UsersDTO getLoggedInUser() {
                UsersDTO user = new UsersDTO();
                user.setUserId(1);
                user.setUsername("test_user");
                return user;
            }

            @Override
            public UsersDTO getUserById(Integer userId) {
                UsersDTO user = new UsersDTO();
                user.setUserId(userId);
                user.setUsername("test_user");
                return user;
            }
        };
    }
}
