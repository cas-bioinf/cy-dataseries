package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.work.Task;

public class NetworkSelectedParameterPassingTaskFactory<TASK extends Task> extends ParameterPassingTaskFactory<TASK>{
	private final CyApplicationManager cyApplicationManager;

	public NetworkSelectedParameterPassingTaskFactory(Class<TASK> taskClass, CyApplicationManager cyApplicationManager, Object... parameters) {
		super(taskClass, parameters);
		this.cyApplicationManager = cyApplicationManager;
	}

	@Override
	public boolean isReady() {
		return cyApplicationManager.getCurrentNetwork() != null;
	}

}
