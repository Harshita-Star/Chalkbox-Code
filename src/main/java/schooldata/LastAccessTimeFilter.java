package schooldata;
import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LastAccessTimeFilter implements Filter {



	private static final String LAST_ACCESS_TIME = "LAST_ACCESS_TIME";
	private static final Long MAX_IDLE_TIME=1L;//30 Mins
	private static final String IS_SESSION_TIMEOUT_HEADER_KEY = "IS_SESSION_TIMEOUT";
	private static final String AJAX_POLL_HEADER_KEY = "IS_AJAX_POLL";

	/**
	 * @param servletRequest
	 * @param servletResponse
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession(false);
		boolean isAjaxPollRequest = session != null && "Y".equalsIgnoreCase(request.getHeader(AJAX_POLL_HEADER_KEY));
		if (session != null && !isAjaxPollRequest) {
			session.setAttribute(LAST_ACCESS_TIME, new Date().getTime());
		} else if (isAjaxPollRequest) {
			Long lastAccessTime = (Long) session.getAttribute(LAST_ACCESS_TIME);
			Long currentTime = new Date().getTime();
			if (lastAccessTime != null) {
				if (getDiffInMins(lastAccessTime, currentTime) >= MAX_IDLE_TIME) {
					response.addHeader(IS_SESSION_TIMEOUT_HEADER_KEY, "Y");
					return;
				}
				response.addHeader(IS_SESSION_TIMEOUT_HEADER_KEY, "N");
			} else {
				chain.doFilter(servletRequest, servletResponse);
			}
		}
	}

	private Long getDiffInMins(Long lastAccessTime, Long currentTime) {
		Long diff = currentTime - lastAccessTime;
		Long diffInSeconds = diff * 1000;// In Seconds
		Long diffInMins = diffInSeconds / 60;// In Minutes
		return diffInMins;
	}

	/**
	 * @param arg0
	 * @throws ServletException
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}


	@Override
	public void destroy() {

	}

}