package my.bank;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/atm/menu.html", "/atm/transactions.html"})

public class AuthFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if (req.getSession().getAttribute("auth") == null) {
			System.out.println("No session auth attribute");
			res.sendRedirect("/Bank42/atm/atm.html");
		} else if (req.getSession().getAttribute("auth").equals("AUTHED")) {
			System.out.println("Looks like you're authed");
			chain.doFilter(request, response);
		} else {
			System.out.println("Something strange happened, but you are not authed");
			res.sendRedirect("/Bank42/atm/atm.html");
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
