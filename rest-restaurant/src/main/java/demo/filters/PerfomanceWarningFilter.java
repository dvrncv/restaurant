package demo.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;

@Component
@Order(2)
public class PerfomanceWarningFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PerfomanceWarningFilter.class);
    private static final long SLOW_REQUEST_THRESHOLD_MS = 20;
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, servletResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            if (duration > SLOW_REQUEST_THRESHOLD_MS) {
                String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
                if (!StringUtils.hasText(correlationId)) {
                    correlationId = "unknown";
                }

                log.warn("Slow request detected: {} {} took {}ms (correlationId: {})",
                        request.getMethod(), request.getRequestURI(), duration, correlationId);
            }
        }
    }
}