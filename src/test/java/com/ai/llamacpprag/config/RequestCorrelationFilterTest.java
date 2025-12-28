package com.ai.llamacpprag.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class RequestCorrelationFilterTest {

    @Test
    void doFilter_WithHeader() throws Exception {
        RequestCorrelationFilter filter = new RequestCorrelationFilter();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader(RequestCorrelationFilter.HEADER)).thenReturn("test-id");

        // We can't easily assert MDC content inside the filter execution unless we hook
        // into the chain or use a spy.
        // But we can verify chain execution.
        // To verify MDC, we can mock the chain to check MDC inside the call.

        doAnswer(invocation -> {
            assertEquals("test-id", MDC.get("correlationId"));
            return null;
        }).when(chain).doFilter(req, res);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        // Verify MDC is cleared
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void doFilter_WithoutHeader() throws Exception {
        RequestCorrelationFilter filter = new RequestCorrelationFilter();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getHeader(RequestCorrelationFilter.HEADER)).thenReturn(null);

        doAnswer(invocation -> {
            String cid = MDC.get("correlationId");
            // Must be present and not empty
            if (cid == null || cid.isBlank()) {
                throw new AssertionError("Correlation ID should be generated");
            }
            return null;
        }).when(chain).doFilter(req, res);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(MDC.get("correlationId"));
    }
}
