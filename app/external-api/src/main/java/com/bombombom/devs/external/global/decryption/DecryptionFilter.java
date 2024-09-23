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
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@WebFilter(urlPatterns = {"/api/v1/auth"})
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
