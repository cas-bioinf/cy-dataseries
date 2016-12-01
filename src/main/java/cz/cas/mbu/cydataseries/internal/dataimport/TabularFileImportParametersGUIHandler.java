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

import cz.cas.mbu.cydataseries.internal.ui.TabularImportParametersPanel;

public class TabularFileImportParametersGUIHandler extends AbstractGUITunableHandler {

	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	
	private static final int NUM_LINES_FOR_PREVIEW = 100;
	
	private TabularImportParametersPanel dataSeriesImportOptionsPanel;
		
	public TabularFileImportParametersGUIHandler(Field field, Object instance, Tunable tunable) {
		super(field, instance, tunable);
		init();
	}

	public TabularFileImportParametersGUIHandler(Method getter, Method setter, Object instance, Tunable tunable) {
		super(getter, setter, instance, tunable);
		init();
	}

	private void init() {
		
		try {
			TabularFileImportParameters params = (TabularFileImportParameters) getValue();
			
			try (Stream<String> lines = Files.lines(params.getFile().toPath())){
				List<String> firstLinesList = lines.limit(NUM_LINES_FOR_PREVIEW).collect(Collectors.toList());
				
				String firstLines = String.join("\r\n", firstLinesList);
				boolean rawDataTruncated = (firstLinesList.size() == NUM_LINES_FOR_PREVIEW); //an imperfect guess but should mostly work
				
				dataSeriesImportOptionsPanel = new TabularImportParametersPanel();
				dataSeriesImportOptionsPanel.setPreviewData(firstLines, rawDataTruncated);
				dataSeriesImportOptionsPanel.setInputfile(params.getFile());			
				
				panel = dataSeriesImportOptionsPanel;
			}
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
				setValue(dataSeriesImportOptionsPanel.getDataSeriesImportParameters());
			} catch(IllegalAccessException | InvocationTargetException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
}
