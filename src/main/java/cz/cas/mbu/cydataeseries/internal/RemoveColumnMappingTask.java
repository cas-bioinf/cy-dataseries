package cz.cas.mbu.cydataeseries.internal;

import java.io.IOException;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.DataSeriesMappingManager.MappingDescriptor;

public class RemoveColumnMappingTask extends AbstractTask implements TunableValidator{

	@Tunable(description="Mapping to remove")
	public ListSingleSelection<MappingDescriptor> targetMapping;
	
	private final DataSeriesMappingManager mappingManager;
	
	
	
	public RemoveColumnMappingTask(DataSeriesMappingManager mappingManager) {
		super();
		this.mappingManager = mappingManager;
		targetMapping = new ListSingleSelection<>(mappingManager.getAllMappingDescriptors());
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		MappingDescriptor descriptor = targetMapping.getSelectedValue();		
		mappingManager.unmapTableColumn(descriptor.getTargetClass(), descriptor.getColumnName());		
	}

	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		try {
			if(targetMapping.getSelectedValue() == null)
			{
				errMsg.append("There is no mapping present.");
				return ValidationState.INVALID;
			}
		}
		catch(IOException ex)
		{
			return ValidationState.INVALID;
		}
			
		return ValidationState.OK;
	}

	
}
