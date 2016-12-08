package cz.cas.mbu.cydataseries.internal.dataimport;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.AbstractGUITunableHandler;

import cz.cas.mbu.cydataseries.internal.ui.SoftFileImportParametersPanel;
import cz.cas.mbu.cydataseries.internal.ui.TabularImportParametersPanel;

public class SoftFileImportParametersGUIHandler extends AbstractGUITunableHandler {

	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME);
	private final Logger logger = Logger.getLogger(SoftFileImportParametersGUIHandler.class);
	
	private SoftFileImportParametersPanel importOptionsPanel;
		
	public SoftFileImportParametersGUIHandler(Field field, Object instance, Tunable tunable) {
		super(field, instance, tunable);
		init();
	}

	public SoftFileImportParametersGUIHandler(Method getter, Method setter, Object instance, Tunable tunable) {
		super(getter, setter, instance, tunable);
		init();
	}

	private void init() {
		try {
			SoftFileImportParameters params = (SoftFileImportParameters) getValue();
						
				importOptionsPanel = new SoftFileImportParametersPanel();
				importOptionsPanel.setData(params.getSelectedTable());
				
				panel = importOptionsPanel;
		} catch(Exception ex)
		{
			panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("Error getting value: " + ex.getMessage()));
			
			userLogger.error("Could not create SOFT file import GUI", ex);
			logger.error("Could not create SOFT file import GUI", ex);
		}
	}
	
	@Override
	public void handle() {		
		if(importOptionsPanel != null)
		{
			try {
				setValue(importOptionsPanel.getSofFileImportParameters());
			} catch(IllegalAccessException | InvocationTargetException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
}
