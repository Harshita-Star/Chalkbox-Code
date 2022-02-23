package schooldata;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UrlFilter implements Filter
{
	public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException
	{

		String url=((HttpServletRequest) request).getServletPath();
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(30*2); // 5 minutes.
		if(url.contains("LoginPage.xhtml"))
		{
			chain.doFilter(request, response);
		}
		else
		{
			HttpSession ss= ((HttpServletRequest) request).getSession(false);
			if(ss==null)
			{
				((HttpServletResponse) response).sendRedirect("LoginPage.xhtml");
			}
			else
			{
				chain.doFilter(request, response);
			}
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}
}
