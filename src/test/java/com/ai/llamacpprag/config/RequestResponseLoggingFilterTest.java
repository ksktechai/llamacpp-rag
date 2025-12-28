package com.ai.llamacpprag.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class RequestResponseLoggingFilterTest {

    @Test
    void doFilterInternal_LogsAndCopiesResponse() throws Exception {
        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
        req.setQueryString("param=value");
        req.setContent("Request Body".getBytes());

        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        // Mock chain to write to response
        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                // Controller writes response
                wrapper.getWriter().write("Response Body");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        // Verify response content matches what was written by chain
        assertEquals("Response Body", res.getContentAsString());
    }
}
