package tk.khumps.ps2census;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class App extends JFrame {
	public DBConnect dbc;
	public ResultPanel results;
	public Statement statement;
	JScrollPane pane;
	QueryBuilder qb;
	JButton updateQuery;
	JButton submitQuery;
	JLabel status;
	JTextField query;

	public App() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		results = new ResultPanel();
		pane = new JScrollPane(results);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(pane);
		qb = new QueryBuilder();
		add(qb, BorderLayout.EAST);
		query = new JTextField("Query");
		query.setMaximumSize(QueryBuilder.listSize);
		add(query, BorderLayout.SOUTH);
		updateQuery = JUtils.newButton(new Dimension(200, 25), "Query", "query", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(qb.buildQuery());
				updateQuery();
			}
		}, qb);
		submitQuery = JUtils.newButton(new Dimension(200, 25), "Submit Query", null, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				query();
			}
		}, qb);
		status = new JLabel("Not Connected");
		qb.add(status, BorderLayout.EAST);
		pack();
		setVisible(true);
		connectDB();
	}

	public void query(String query) {
		if (dbc != null) {
			ResultSet rs = dbc.query(statement, query);
			String[][] a = new String[5][5];
			if (rs != null)
				a = DBConnect.toArray(dbc.getResults(rs));
			remove(pane);
			results = new ResultPanel(a, dbc.getHeader(rs));
			pane = new JScrollPane(results);
			add(pane);
			setVisible(true);
		}
	}

	public void updateQuery() {
		query.setText(qb.buildQuery());
	}

	public void connectDB() {
		status.setText("Connecting...");
		dbc = new DBConnect("jdbc:mysql://ts.khumps.tk:3306", "query_user", "planetside");
		while (dbc == null) {
		}
		status.setText("Connected");
		statement = dbc.createStatement();
	}

	public void query() {
		query(query.getText());
	}

	public static void main(String[] args) {
		new App();
	}

}