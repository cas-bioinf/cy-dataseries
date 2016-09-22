package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;

public class UIUtils {
	public static void ensurePanelVisible(CyServiceRegistrar registrar, CytoPanelComponent panel)
	{
		CytoPanel cytoPanel = registrar.getService(CySwingApplication.class).getCytoPanel(panel.getCytoPanelName());
		if(cytoPanel.getState() == CytoPanelState.HIDE)
		{
			cytoPanel.setState(CytoPanelState.DOCK);
		}
		
		SwingUtilities.invokeLater(() -> {
			int index = cytoPanel.indexOfComponent(panel.getComponent());
			if(index >= 0)
			{
				cytoPanel.setSelectedIndex(index);;
			}
			
		});
		
	}
	
	public static DocumentListener listenForAllDocumentChanges(Runnable action)
	{
		return new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				action.run();				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				action.run();				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				action.run();				
			}
		};
	}
}
