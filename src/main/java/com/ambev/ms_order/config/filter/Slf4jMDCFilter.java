package com.ambev.ms_order.config.filter;

import static com.ambev.ms_order.commons.constants.Constants.LOG_TRACE_ID;
import static java.util.UUID.randomUUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = false)
@Component
@RequiredArgsConstructor
@Slf4j
public class Slf4jMDCFilter extends OncePerRequestFilter {

	private final ObjectMapper mapper;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) {
		try {
			ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
			ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);
			MDC.put(LOG_TRACE_ID, randomUUID().toString());
			chain.doFilter(req, resp);
			resp.copyBodyToResponse();
		} catch (Exception ex) {
			log.error("Error", "Exception occurred in filter while setting UUID for logs", ex);
		} finally {
			MDC.remove(LOG_TRACE_ID);
		}
	}

	@Override
	protected boolean isAsyncDispatch(final HttpServletRequest request) {
		return false;
	}

	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}
}