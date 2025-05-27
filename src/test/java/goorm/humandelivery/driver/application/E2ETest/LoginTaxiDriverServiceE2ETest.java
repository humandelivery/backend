package goorm.humandelivery.driver.application.E2ETest;

import goorm.humandelivery.driver.application.LoginTaxiDriverService;
import goorm.humandelivery.driver.application.port.out.SaveTaxiDriverPort;
import goorm.humandelivery.driver.domain.TaxiDriver;
import goorm.humandelivery.driver.dto.request.LoginTaxiDriverRequest;
import goorm.humandelivery.global.exception.IncorrectPasswordException;
import goorm.humandelivery.global.exception.JwtTokenGenerationException;
import goorm.humandelivery.global.exception.DriverEntityNotFoundException;
import goorm.humandelivery.shared.dto.response.JwtResponse;
import goorm.humandelivery.shared.dto.response.TokenInfoResponse;
import goorm.humandelivery.shared.security.port.out.JwtTokenProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(LoginTaxiDriverServiceE2ETest.FailingJwtTokenProviderConfig.class)
class LoginTaxiDriverServiceE2ETest {

    @Autowired
    private LoginTaxiDriverService loginTaxiDriverService;

    @Autowired
    private SaveTaxiDriverPort saveTaxiDriverPort;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final String validLoginId = "testdriver";
    private final String rawPassword = "password123";

    @BeforeEach
    void setUp() {
        TaxiDriver driver = TaxiDriver.builder()
                .loginId(validLoginId)
                .password(passwordEncoder.encode(rawPassword))
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .build();
        saveTaxiDriverPort.save(driver);
    }

    @Test
    @DisplayName("1. 로그인 성공")
    void loginSuccess() {
        JwtResponse response = loginTaxiDriverService.login(
                LoginTaxiDriverRequest.builder()
                        .loginId(validLoginId)
                        .password(rawPassword)
                        .build());

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    @DisplayName("2. 로그인 실패 - 존재하지 않는 ID")
    void loginFailNotFound() {
        LoginTaxiDriverRequest request = LoginTaxiDriverRequest.builder()
                .loginId("nonexistent")
                .password(rawPassword)
                .build();

        assertThatThrownBy(() -> loginTaxiDriverService.login(request))
                .isInstanceOf(DriverEntityNotFoundException.class);
    }

    @Test
    @DisplayName("3. 로그인 실패 - 비밀번호 불일치")
    void loginFailWrongPassword() {
        LoginTaxiDriverRequest request = LoginTaxiDriverRequest.builder()
                .loginId(validLoginId)
                .password("wrongpassword")
                .build();

        assertThatThrownBy(() -> loginTaxiDriverService.login(request))
                .isInstanceOf(IncorrectPasswordException.class);
    }

    @Test
    @DisplayName("4. JWT 토큰 생성 실패 시 예외 발생")
    void loginFailJwtGenerationException() {
        LoginTaxiDriverRequest request = LoginTaxiDriverRequest.builder()
                .loginId(validLoginId)
                .password(rawPassword)
                .build();

        assertThatThrownBy(() -> loginTaxiDriverService.login(request))
                .isInstanceOf(JwtTokenGenerationException.class)
                .hasMessageContaining("JWT 토큰 발급에 실패했습니다");
    }

    @TestConfiguration
    static class FailingJwtTokenProviderConfig {
        @Bean
        public JwtTokenProviderPort jwtTokenProviderPort() {
            return new JwtTokenProviderPort() {
                @Override
                public String generateToken(String loginId) {
                    throw new JwtTokenGenerationException("JWT 토큰 발급에 실패했습니다.", new RuntimeException());
                }

                // JwtTokenProviderPort 인터페이스에 존재하는 나머지 추상 메서드들도 모두 구현해줘야 합니다.
                @Override
                public boolean validateToken(String token) {
                    // 적당한 더미 구현 또는 예외 던지기
                    return false;
                }

                @Override
                public TokenInfoResponse extractTokenInfo(String token){
                    // 적당한 더미 구현 또는 예외 던지기
                    return null;
                }

                @Override
                public Authentication getAuthentication(String token){
                    return null;
                }
                // 만약 더 추상 메서드가 있다면 여기에 추가 구현 필요
            };
        }
    }


}

