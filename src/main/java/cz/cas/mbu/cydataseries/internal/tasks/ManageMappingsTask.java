package cz.cas.mbu.cydataseries.internal.tasks;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import cz.cas.mbu.cydataseries.internal.ui.MappingManagerPanel;

public class ManageMappingsTask extends AbstractTask {

	private final CyServiceRegistrar registrar;
	
		
	public ManageMappingsTask(CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}


	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		SwingUtilities.invokeLater( () -> {
			final JFrame parent = registrar.getService(CySwingApplication.class).getJFrame();
			final JDialog frm = new JDialog(parent, "Manage data series mappings");
			frm.setLayout(new BorderLayout());
			frm.add(new MappingManagerPanel(registrar), BorderLayout.CENTER);
			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(e -> frm.setVisible(false));
			frm.add(closeButton, BorderLayout.SOUTH);
			frm.pack();			
			frm.setModal(true);
			int x = (parent.getWidth() - frm.getWidth()) / 2;
			int y = (parent.getHeight() - frm.getHeight()) / 2;
			frm.setLocation(parent.getX() + x, parent.getY() + y);
			frm.setVisible(true);		
		});
	}

}
