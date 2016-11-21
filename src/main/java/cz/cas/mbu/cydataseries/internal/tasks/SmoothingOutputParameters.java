package cz.cas.mbu.cydataseries.internal.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator.ValidationState;

import cz.cas.mbu.cydataseries.DataSeriesMappingManager;
import cz.cas.mbu.cydataseries.MappingDescriptor;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.Utils;

public class SmoothingOutputParameters {

	@Tunable(description="Name of the resulting series", groups ="Output")
	public String resultName;
	
	@Tunable(description="Map the resulting series to the same nodes/edges as the original", groups ="Output")
	public boolean mapResult;
	
	@Tunable(description="Replace the mapping of the original series with the new series",dependsOn="mapResult=true", groups ="Output")
	public boolean replaceMapping = true;
	
	@Tunable(description="Suffix added to the newly created mapping columns", dependsOn="replaceMapping=false", groups ="Output")
	public String mappingSuffix;

	public ValidationState getValidationState(TimeSeries sourceTimeSeries, CyServiceRegistrar registrar, StringBuilder errMsg) {
		if(resultName.isEmpty())
		{
			errMsg.append("You have to specify a name for the new time series.");
			return ValidationState.INVALID;
		}
		if(mapResult && !replaceMapping && mappingSuffix.length() < 1){
			errMsg.append("You have to specify a mapping suffix");
			return ValidationState.INVALID;
		}
		
		if(mapResult && !replaceMapping)
		{
			final DataSeriesMappingManager mappingManager = registrar.getService(DataSeriesMappingManager.class);
			List<MappingDescriptor<TimeSeries>> allDescriptors = mappingManager.getMappingDescriptorsForSeries(sourceTimeSeries);
			
			List<String> existingColumns = new ArrayList<>();
			List<String> conflicitingTypeColumns = new ArrayList<>();
			
			allDescriptors.forEach(descriptor -> {
				String newColumnName = descriptor.getColumnName() + mappingSuffix;			
				CyTable defaultTable = Utils.getDefaultTable(registrar, descriptor.getTargetClass());
				CyColumn newColumn = defaultTable.getColumn(newColumnName);
				if (newColumn != null)
				{
					if (newColumn.getType().equals(DataSeriesMappingManager.MAPPING_COLUMN_CLASS))
					{
						existingColumns.add(newColumnName);
					}
					else
					{
						conflicitingTypeColumns.add(newColumnName);
					}
				}			
			});
			
			if (!conflicitingTypeColumns.isEmpty())
			{
				String columnNames = conflicitingTypeColumns.stream().collect(Collectors.joining(", "));
				errMsg.append("Columns " + columnNames + " already exist but have type incompatible with data series mapping.");
				return ValidationState.INVALID;
			}
			else if (!existingColumns.isEmpty())
			{
				String columnNames = existingColumns.stream().collect(Collectors.joining(", "));
				errMsg.append("Columns " + columnNames + " already exist do you want to overwrite their contents?");
				return ValidationState.REQUEST_CONFIRMATION;				
			}
			
			
		}
		
		if(mapResult && replaceMapping)
		{
			errMsg.append("Do you really want to override existing mappings for series '" + sourceTimeSeries.getName() + "'?");
			return ValidationState.REQUEST_CONFIRMATION;
		}
		return ValidationState.OK;
	}	
}
