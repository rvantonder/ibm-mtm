package my.bank;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ATM
 */
@WebServlet("/ATM")

public class ATM extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection db2Conn;
	private static Statement stmt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ATM() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	private static boolean validateInput(String accountIn, String pinIn,
			String zipIn) throws SQLException {
		boolean validPin = false;
		boolean validZip = false;
		String query;
		ResultSet result;

    // TODO use prepared statements
		query = "SELECT city,state FROM CUSTOMER WHERE ACCT = '" + accountIn + "'";
		System.out.println("Erroring/successing query is: " + query);
		result = stmt.executeQuery(query);
		result.next();
		String customerResultCity = result.getString("city");
		String customerResultState = result.getString("state");
		
    // TODO use prepared statements
		query = "SELECT pin FROM PIN WHERE ACCT = " + accountIn;
		result = stmt.executeQuery(query);
		result.next();
		String pinResultPin = result.getString("pin");
		
		if (pinIn.equals(pinResultPin)) {
			validPin = true;
		};

    // TODO use prepared statements
		query = "SELECT zip FROM ZBANK.ZIPCODE WHERE ZCITY = '"
				+ customerResultCity.toUpperCase() + "' AND ZSTATE = '"
				+ customerResultState + "'";
		result = stmt.executeQuery(query);
		

		
		while (result.next()) {
			String zipResultZip = result.getString("zip");
			if (zipIn.equals(zipResultZip)) {
				validZip = true;
			}
		}
		
		if (validPin && validZip) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean validateInputWithOtp(String accountIn, String pinIn,
			String zipIn, String otp) throws SQLException {
		boolean validPin = false;
		boolean validZip = false;
		boolean validOtp = false;
		String query;
		ResultSet result;

    // TODO use prepared statements
		query = "SELECT city,state FROM CUSTOMER WHERE ACCT = '" + accountIn +"'";
		result = stmt.executeQuery(query);
		result.next();
		String customerResultCity = result.getString("city");
		String customerResultState = result.getString("state");

    // TODO use prepared statements
		query = "SELECT pin FROM PIN WHERE ACCT = " + accountIn;
		result = stmt.executeQuery(query);
		result.next();
		String pinResultPin = result.getString("pin");
		if (pinIn.equals(pinResultPin)) {
			validPin = true;
		};

    // TODO use prepared statements
		query = "SELECT zip FROM ZBANK.ZIPCODE WHERE ZCITY = '"
				+ customerResultCity.toUpperCase() + "' AND ZSTATE = '"
				+ customerResultState + "'";
		result = stmt.executeQuery(query);
		while (result.next()) {
			String zipResultZip = result.getString("zip");
			if (zipIn.equals(zipResultZip)) {
				validZip = true;
			}
		}
		
		validOtp = Util.validateOTP(accountIn, otp);
		Util.discardOTP(accountIn);

		if (validPin && validZip && validOtp) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String accountIn = request.getParameter("account");
		String pinIn = request.getParameter("pin");
		String zipIn = request.getParameter("zip");
		String otpIn = request.getParameter("otp");
		Boolean validInput = false;
		try {
			if (accountIn.equals("123456123456")) { // for DEMO purposes
				validInput = validateInputWithOtp(accountIn, pinIn, zipIn,
						otpIn);
			} else {
				validInput = validateInput(accountIn, pinIn, zipIn);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		if (validInput) {
			request.getSession().setAttribute("auth", "AUTHED"); 
			request.getSession().setAttribute("account", accountIn); 
			response.setStatus(200);
		} else {
			response.sendError(403);
		}
	}

	public void init(ServletConfig config) throws ServletException {
    /**
     * Removed login credentials for security
     */
	}

	public void destroy() {
    /**
     * Removed
     */
	}

}
