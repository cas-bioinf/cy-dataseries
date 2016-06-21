package cz.cas.mbu.cytimeseries.internal.dataimport;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.AbstractGUITunableHandler;

import cz.cas.mbu.cytimeseries.internal.ui.ImportDataSeriesPanel;

public class ImportParametersGUIHandler extends AbstractGUITunableHandler {

	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	
	private static final int NUM_LINES_FOR_PREVIEW = 100;
	
	private ImportDataSeriesPanel dataSeriesImportOptionsPanel;
		
	public ImportParametersGUIHandler(Field field, Object instance, Tunable tunable) {
		super(field, instance, tunable);
		init();
	}

	public ImportParametersGUIHandler(Method getter, Method setter, Object instance, Tunable tunable) {
		super(getter, setter, instance, tunable);
		init();
	}

	private void init()
	{
		
		try {
			ImportParameters params = (ImportParameters) getValue();
			Stream<String> lines = Files.lines(params.getFile().toPath());
			String firstLines = lines.limit(NUM_LINES_FOR_PREVIEW).collect(Collectors.joining("\r\n"));
			
			dataSeriesImportOptionsPanel = new ImportDataSeriesPanel();
			dataSeriesImportOptionsPanel.setPreviewData(firstLines);
			
			panel = dataSeriesImportOptionsPanel;
		} catch (Exception ex) {
			panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("Error processing preview: " + ex.getMessage()));
			userLogger.error(ex);
		} 	
	}
	
	@Override
	public void handle() {		
		if(dataSeriesImportOptionsPanel != null)
		{
			try {
				setValue(dataSeriesImportOptionsPanel.getImportParameters());
			} catch(IllegalAccessException | InvocationTargetException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
}
