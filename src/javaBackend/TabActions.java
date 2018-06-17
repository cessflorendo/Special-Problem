package javaBackend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTabbedPane;

public class TabActions implements ActionListener{
	private JTabbedPane pane;
	private int active;
	
	public TabActions(JTabbedPane pane, int active){
		this.setPane(pane);
		this.setActive(active);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i=0; i<pane.getTabCount(); i++){
			if (i==active){
				pane.setEnabledAt(i, true);
				pane.setSelectedIndex(i);
			} else pane.setEnabledAt(i, false);
		}
	}

	public JTabbedPane getPane() {
		return pane;
	}

	public void setPane(JTabbedPane pane) {
		this.pane = pane;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
}

