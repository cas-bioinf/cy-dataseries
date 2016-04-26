package cz.cas.mbu.cytimeseries.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;

import cz.cas.mbu.cytimeseries.TimeSeriesManager;

public abstract class AbstractTimeSeriesRelatedTask extends AbstractTask {

	protected final CyNetwork network;
	protected final TimeSeriesManager manager;

	public AbstractTimeSeriesRelatedTask(CyApplicationManager cyApplicationManager, TimeSeriesManager manager) {
		super();
		network = cyApplicationManager.getCurrentNetwork();
		this.manager = manager;
	}

}