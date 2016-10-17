package tk.khumps.ps2census;

import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

public class ResultPanel extends JTable {
	public ResultPanel(String[][] data, String[] headers) {
		super(data, headers);
		setFillsViewportHeight(true);
		
		setVisible(true);
	}

	public ResultPanel() {
		this(new String[][] { { "", "", "" }, { "", "", "" } }, new String[] { "", "", "" });
	}
}