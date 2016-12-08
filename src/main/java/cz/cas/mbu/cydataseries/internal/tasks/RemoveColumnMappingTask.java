package cz.cas.mbu.cydataseries.internal.tasks;

import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.MappingDescriptor;


public class RemoveColumnMappingTask extends AbstractValidatedTask {

	@Tunable(description="Mapping to remove")
	public ListSingleSelection<MappingDescriptor<?>> targetMapping;
	
	private final DataSeriesMappingManager mappingManager;
	

	public RemoveColumnMappingTask(DataSeriesMappingManager mappingManager) {
		super();
		this.mappingManager = mappingManager;
		targetMapping = new ListSingleSelection<>(mappingManager.getAllMappingDescriptors());	
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Remove column mapping to Data series";
	}	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		MappingDescriptor<?> descriptor = targetMapping.getSelectedValue();		
		mappingManager.unmap(descriptor);		
	}

	@Override
	public ValidationState getValidationState(StringBuilder errMsg) {
		if(targetMapping.getSelectedValue() == null) {
			errMsg.append("There is no mapping present.");
			return ValidationState.INVALID;
		}

		return ValidationState.OK;
	}

}
