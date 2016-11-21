package cz.cas.mbu.cydataseries.internal;

import java.util.Optional;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.CyServiceRegistrar;

import cz.cas.mbu.cydataseries.DataSeriesException;

public class Utils {
	public static CyTable getDefaultTable(CyServiceRegistrar registrar, Class<? extends CyIdentifiable> targetClass)
	{
		final CyNetworkTableManager networkTableManager = registrar.getService(CyNetworkTableManager.class);
		CyTableManager tableManager = registrar.getService(CyTableManager.class);
		Optional<CyTable> defaultTable = tableManager.getLocalTables(targetClass).stream()
				.filter(table -> networkTableManager.getTableNamespace(table).equals(CyNetwork.DEFAULT_ATTRS)).findAny();
		
		if (!defaultTable.isPresent())
		{
			throw new DataSeriesException("Could not find default table for " + targetClass.getSimpleName());
		}
		
		return defaultTable.get();
		
	}
}
