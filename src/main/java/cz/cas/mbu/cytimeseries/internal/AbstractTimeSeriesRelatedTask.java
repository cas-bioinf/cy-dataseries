package cz.cas.mbu.cytimeseries.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;

import cz.cas.mbu.cytimeseries.DataSeriesManager;

public abstract class AbstractTimeSeriesRelatedTask extends AbstractTask {

	protected final CyNetwork network;
	protected final DataSeriesManager manager;

	public AbstractTimeSeriesRelatedTask(CyApplicationManager cyApplicationManager, DataSeriesManager manager) {
		super();
		network = cyApplicationManager.getCurrentNetwork();
		this.manager = manager;
	}

}