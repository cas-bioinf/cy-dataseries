package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.common.primitives.Doubles;

import cz.cas.mbu.cydataseries.DataSeriesManager;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.dataimport.MatlabSyntaxNumberList;
import cz.cas.mbu.cydataseries.internal.ui.SmoothingPreviewPanel;
import cz.cas.mbu.cydataseries.internal.ui.UIUtils;

public class SmoothInteractiveShowUITask extends AbstractValidatedTask {
	
	@Tunable(description="Series to smooth")
	public ListSingleSelection<TimeSeries> timeSeries;
	
	
	private final CyServiceRegistrar registrar;
	
	public SmoothInteractiveShowUITask(CyServiceRegistrar registrar) {
		this.registrar = registrar;
		timeSeries = new ListSingleSelection<>(registrar.getService(DataSeriesManager.class).getDataSeriesByType(TimeSeries.class));
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		SmoothingPreviewPanel panel = new SmoothingPreviewPanel(registrar, timeSeries.getSelectedValue());
	
		registrar.registerService(panel, CytoPanelComponent.class, new Properties());
		
		UIUtils.ensurePanelVisible(registrar, panel);		
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(timeSeries.getSelectedValue() == null)
		{
			errMsg.append("You have to select an input time series");
			return ValidationState.INVALID;
		}	
		return ValidationState.OK;
	}
		
}
