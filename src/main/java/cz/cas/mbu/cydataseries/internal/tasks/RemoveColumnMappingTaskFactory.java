package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cydataseries.DataSeriesMappingManager;


public class RemoveColumnMappingTaskFactory extends AbstractTaskFactory {

	private final DataSeriesMappingManager mappingManager;

	public RemoveColumnMappingTaskFactory(DataSeriesMappingManager mappingManager) {
		super();
		this.mappingManager = mappingManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new RemoveColumnMappingTask(mappingManager));
	}

	@Override
	public boolean isReady() {
		return !mappingManager.isMappingsEmpty();
	}

}
