package tk.khumps.ps2census;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

public class QueryBuilder extends JPanel {

	public static enum EVENT_TYPE {
		AchievementEarned, BattleRankUp, ContinentLock, ContinentUnlock, Death, FacilityControl, GainExperience, ItemAdded, PlayerFacilityCapture, PlayerFacilityDefended, PlayerLogin, SkillAdded, VehicleDestroy
	};

	public enum SERVER {
		Briggs(25), Connery(1), Jaeger(19), Miller(10), Cobalt(13), Emerald(17);

		public final int id;

		SERVER(int id) {
			this.id = id;
		}

	}

	JList servers;
	JComboBox<EVENT_TYPE> events;
	SpinnerListModel hours;
	SpinnerListModel minutes;
	JTextField limit;
	JTextField dateLo;
	JSpinner hrsLo;
	JSpinner minLo;
	JTextField dateHi;
	JSpinner hrsHi;
	JSpinner minHi;
	JButton query;
	public static final Dimension listSize = new Dimension(99999, 150);
	public static final Dimension spinnerSize = new Dimension(999999, 20);

	public QueryBuilder() {
		BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
		servers = new JList<SERVER>(SERVER.values());
		servers.setMaximumSize(listSize);
		events = new JComboBox<EVENT_TYPE>(EVENT_TYPE.values());
		events.setMaximumSize(new Dimension(9999, 20));
		initDateTime();
		limit = new JTextField("limit");
		limit.setMaximumSize(spinnerSize);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(servers);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(events);
		add(Box.createRigidArea(new Dimension(0, 10)));
		// add(hrs);
		add(Box.createRigidArea(new Dimension(0, 10)));
		// add(min);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(limit);
		add(Box.createRigidArea(new Dimension(0, 10)));
	}

	private void initDateTime() {
		hours = new SpinnerListModel(getHours());
		minutes = new SpinnerListModel(getMinutes());

		dateLo = new JTextField("2016/1/1");
		dateLo.setMaximumSize(spinnerSize);
		hrsLo = new JSpinner(hours);
		hrsLo.setToolTipText("Lowest time(hours) to search for");
		hrsLo.setMaximumSize(spinnerSize);
		minLo = new JSpinner(minutes);
		minLo.setToolTipText("Lowest time(minutes) to search for");
		minLo.setMaximumSize(spinnerSize);
		add(dateLo);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(hrsLo);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(minLo);
		add(Box.createRigidArea(new Dimension(0, 10)));

		dateHi = new JTextField("2017/1/1");
		dateHi.setMaximumSize(spinnerSize);
		hrsHi = new JSpinner(hours);
		hrsHi.setToolTipText("Highest time(hours) to search for");
		hrsHi.setMaximumSize(spinnerSize);
		minHi = new JSpinner(minutes);
		minHi.setToolTipText("Highest time(minutes) to search for");
		minHi.setMaximumSize(spinnerSize);
		add(dateHi);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(hrsHi);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(minHi);

	}

	public String buildQuery() {
		return "SELECT * FROM ps2_census." + (EVENT_TYPE) events.getSelectedItem() + " " + parseWhere() + " "
				+ order(false) + " " + resultLimit("", limit.getText());
	}

	public String parseWhere() {
		String servers = serverLimit(this.servers.getSelectedValuesList());
		String time = timeLimit(parseTime(dateLo.getText(), hrsLo.getValue().toString(), minLo.getValue().toString()),
				parseTime(dateHi.getText(), hrsHi.getValue().toString(), minHi.getValue().toString()));
		if (servers.isEmpty() || time.isEmpty())
			return "WHERE " + servers + time;
		return "WHERE " + servers + " AND " + time;
	}

	public Integer[] getHours() {
		Integer[] hrs = new Integer[25];
		for (int i = 0; i < hrs.length; i++)
			hrs[i] = i;
		return hrs;
	}

	public Integer[] getMinutes() {
		Integer[] min = new Integer[61];
		for (int i = 0; i < min.length; i++)
			min[i] = i;
		return min;
	}

	public static String order(boolean ascending) {
		if (ascending)
			return "ORDER BY 'timestamp'";
		return "ORDER BY 'timestamp' DESC";
	}

	public static String resultLimit(String lo, String hi) {
		if (hi.equals("limit"))
			return "";
		if (lo.isEmpty() && hi.isEmpty())
			return "";
		return "limit " + lo + (lo.isEmpty() ? "" : ", ") + hi;
	}

	public static String timeLimit(String lo, String hi) {
		if (lo.isEmpty() && hi.isEmpty())
			return "";
		return /* "WHERE 'Time EST' BETWEEN " + lo + " AND " + hi; */
		"'Time EST' > " + lo + " AND 'Time EST' > " + hi;
	}

	public static String serverLimit(List<SERVER> servers) {
		if (servers.size() == 0)
			return "";
		return "world_id IN (" + getServers(servers) + ")";
	}

	public String parseTime(int year, int month, int day, int hour, int minute, int second) {
		return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
	}

	public String parseTime(String date, String hour, String minute) {
		try {
			return "'" + date + " "
					+ (Integer.parseInt(hour) < 10 ? "0" + Integer.parseInt(hour) : Integer.parseInt(hour)) + ":"
					+ (Integer.parseInt(minute) < 10 ? "0" + Integer.parseInt(minute) : Integer.parseInt(minute))
					+ ":00'";
		} catch (NumberFormatException e) {
			System.out.println("Invalid parameters");
		}
		return "";
	}

	public static String getServers(List<SERVER> servers) {
		String str = "";
		if (servers.size() > 0) {
			for (int i = 0; i < servers.size() - 1; i++) {
				str += "'" + servers.get(i).id + "' , ";
			}
			str += "'" + servers.get(servers.size() - 1).id + "'";
		}
		return str;
	}
}
