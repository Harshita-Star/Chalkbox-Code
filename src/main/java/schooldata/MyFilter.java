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

public class MyFilter implements Filter {

	String userType="";
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req=(HttpServletRequest) request;
		HttpServletResponse res=(HttpServletResponse) response;

        userType=(String) req.getSession().getAttribute("type");
		
		if(userType !=null)
		{
			if(userType.contains("master_admin"))
			{
				chain.doFilter(request, response);
			}
			else
			{
				req.getSession().invalidate();
				res.sendRedirect(req.getContextPath() + "/ChalkboxLogin.xhtml");
			}
		}
		else
		{
			res.sendRedirect(req.getContextPath() + "/ChalkboxLogin.xhtml");
		}

	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
