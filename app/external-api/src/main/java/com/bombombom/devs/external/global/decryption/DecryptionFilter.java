package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.external.encryption.service.AsymmetricEncryptionService;
import com.bombombom.devs.external.global.decryption.dto.EncryptedRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공개 키로 암호화된 Request Body를 복호화 하는 Filter이다. {@link DecryptionFilterConfig}에서 설정된 URI 경로로 클라이언트 요청이
 * 온 경우에 해당 Filter를 거치게 된다.
 * <p>
 * Request Body는 {@link EncryptedRequest}형태로 구성되어 있다. Request Body를 복호화 하기 위해, 기존 요청을
 * {@link HttpServletRequestWrapper}로 래핑하고,
 * {@link AsymmetricEncryptionService#decryptData(int, long, byte[])}를 호출하여 기존 Request Body를 복호화한다.
 * 이후, 복호화 된 데이터로 Request Body를 재구성하고, 다음 Filter에게 요청을 위임한다.
 * </p>
 *
 * @see <a href="https://github.com/Team-BomBomBom/Server/pull/57">Feat: #BBB-136 로그인 및 회원가입 시
 * 클라이언트와 서버 간 종단간 암호화 적용</a>
 */

@Slf4j
@RequiredArgsConstructor
public class DecryptionFilter implements Filter {

    private final ObjectMapper objectMapper;
    private final AsymmetricEncryptionService asymmetricEncryptionService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        DecryptedRequestWrapper requestWrapper = new DecryptedRequestWrapper(httpRequest);
        EncryptedRequest encryptedRequest = objectMapper.readValue(requestWrapper.getRequestBody(),
            EncryptedRequest.class);
        try {
            requestWrapper.setRequestBody(decryptRequestBody(encryptedRequest));
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (Exception e) {
            log.error("Failed to decrypt request body. Error details: ", e);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private String decryptRequestBody(EncryptedRequest encryptedRequest)
        throws NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return asymmetricEncryptionService.decryptData(encryptedRequest.id(),
            encryptedRequest.version(), encryptedRequest.encryptedData());
    }
}
