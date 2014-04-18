package my.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Create
 */
@WebServlet("/Create")
public class Create extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection db2Conn;
	private static Statement stmt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Create() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int newCustResult = 0;
		int newPinResult = 0;
		int newDepositResult = 0;
		String result = "";
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String address = request.getParameter("street");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String account = request.getParameter("account");
		String PIN = request.getParameter("pin");
		String deposit = request.getParameter("deposit");
		try {
      // TODO use prepared statements
			String newCustomer = String
					.format("INSERT INTO CUSTOMER (ACCT,STATUS,FN,LN,ADDR,CITY,STATE)"
							+ "  VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
							account, "A", firstName, lastName, address, city,
							state);

			newCustResult = stmt.executeUpdate(newCustomer);
      // TODO use prepared statements
			String newPIN = String.format("INSERT INTO PIN (ACCT,PIN)"
					+ "  VALUES ('%s', '%s');", account, PIN);
			newPinResult = stmt.executeUpdate(newPIN);

      // TODO use prepared statements
			String newDeposit = String.format(
					"INSERT INTO BALANCE (ACCT,BALANCE)"
							+ "  VALUES ('%s', '%s');", account, deposit);
			System.out.println(newDeposit);
			newDepositResult = stmt.executeUpdate(newDeposit);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		if (newCustResult == 0 || newPinResult == 0 || newDepositResult == 0) {
			result = "Unable to create new customer, check input fields OR customer already exists!";
		} else {
			result = "New customer successfully created!";
		}
		out.println(result);
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
