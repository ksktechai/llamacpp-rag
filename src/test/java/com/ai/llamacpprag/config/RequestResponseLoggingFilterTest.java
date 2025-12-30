package com.ai.llamacpprag.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class RequestResponseLoggingFilterTest {

    private final RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();

    @Test
    void doFilterInternal_LogsAndCopiesResponse() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/test");
        req.setQueryString("param=value");
        req.setContent("Request Body".getBytes());

        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        // Mock chain to write to response
        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("Response Body");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        // Verify response content matches what was written by chain
        assertEquals("Response Body", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesNullQueryString() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/chat");
        // No query string set - null case
        req.setContent("{}".getBytes());

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("{\"result\": \"ok\"}");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("{\"result\": \"ok\"}", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesEmptyParameters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/status");
        // No parameters set - empty case

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("OK");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("OK", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesMultipleParameters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/search");
        req.setParameter("query", "test");
        req.setParameter("page", "1");
        req.setParameter("limit", "10");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("{\"results\": []}");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("{\"results\": []}", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesMultiValueParameters() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/filter");
        req.setParameter("tags", "java", "spring", "test");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("filtered");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("filtered", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesEncodedQueryString() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/search");
        req.setQueryString("query=hello%20world");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("decoded");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("decoded", res.getContentAsString());
    }

    @Test
    void doFilterInternal_HandlesEmptyRequestBody() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/ping");
        // No content set - empty body

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        doAnswer(invocation -> {
            Object respArg = invocation.getArguments()[1];
            if (respArg instanceof ContentCachingResponseWrapper wrapper) {
                wrapper.getWriter().write("pong");
            }
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(req, res, chain);

        assertEquals("pong", res.getContentAsString());
    }
}
