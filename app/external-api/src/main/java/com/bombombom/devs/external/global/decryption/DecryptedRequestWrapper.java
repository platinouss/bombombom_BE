package com.bombombom.devs.external.global.decryption;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecryptedRequestWrapper extends HttpServletRequestWrapper {

    private String requestBody;

    public DecryptedRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        
        this.requestBody = new String(request.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() {
        final InputStream inputStream = new ByteArrayInputStream(
            requestBody.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                try {
                    return inputStream.available() == 0;
                } catch (IOException e) {
                    return true;
                }
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(this.requestBody));
    }
}
