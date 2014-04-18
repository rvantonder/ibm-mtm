package my.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class OTP
 */
@WebServlet("/OTP")
public class OTP extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OTP() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		System.out.println("OTP SERVLET RECEIVED GET");
		String s = "adkdkdkdsdf";
		out.print(s);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("OTP SERVLET RECEIVED POST");
		PrintWriter out = response.getWriter();
		String accountIn = request.getParameter("account");
		
		String otp = generateOTP();
		Util.setOTP(accountIn, otp);

		out.print(otp);

	}

	private static String generateOTP() {
		String alphabet = "abcdef0123456789";

		Random rnd = new SecureRandom();
		StringBuilder pass = new StringBuilder();
		for (int i = 0; i < 6; i++)
			pass.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
		return pass.toString();
	}

}
