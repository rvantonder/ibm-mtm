package my.bank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import my.bank.Util;

/**
 * Servlet implementation class Service
 */
@WebServlet("/Service")
public class Service extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	private static Connection db2Conn;
//	private static Statement stmt;

	@Resource(name = "jdbc/MyDataSource")
	private DataSource ds1;
	private Connection con = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Service() {
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
		String account = request.getParameter("account");
		String newStatus = request.getParameter("newStatus");

		if (account == null || newStatus == null) {
			return;
		}

		if (!Util.validateAccount(account)) {
			out.println("Invalid Account! Enter 12 numeric characters.");
			return;
		} else if (!Util.validateStatus(newStatus)) {
			out.println("Invalid Status! Enter \"A\" or \"I\".");
			return;
		}

    // TODO use prepared statements
		String updateSQL = "UPDATE CUSTOMER SET STATUS = ? WHERE ACCT = ?";
		int result = 0;
		String msg = "";
		try {
			con = ds1.getConnection();
			PreparedStatement stmt = con.prepareStatement(updateSQL);
			stmt.setString(1,  newStatus);
			stmt.setString(2, account);
			result = stmt.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		if (result == 0) {
			msg = "Unable to change status for account " + account + " to "
					+ newStatus + ".";
		} else {
			msg = "Status for account " + account + " changed to " + newStatus
					+ ".";
		}
		System.out.println("Message response: " + msg);
		out.println(msg);
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
