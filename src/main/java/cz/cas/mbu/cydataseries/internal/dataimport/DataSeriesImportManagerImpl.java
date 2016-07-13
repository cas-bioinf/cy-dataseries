package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportManager;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;


public class DataSeriesImportManagerImpl implements DataSeriesImportManager {

	private final Logger logger = Logger.getLogger(DataSeriesImportManager.class);
	private final ServiceTracker providerTracker;

	public DataSeriesImportManagerImpl(BundleContext bc) {
		providerTracker = new ServiceTracker(bc, DataSeriesImportProvider.class.getName(), null);
		providerTracker.open();
	}
	
	@Override
	public List<DataSeriesImportProvider> getAllImportProviders() {
		Object[] providerObjects = providerTracker.getServices();
		List<DataSeriesImportProvider> providers = new ArrayList<>();
		
		if(providerObjects == null) {
			return providers;
		}
		
		for(Object obj : providerObjects) {
			try {
				DataSeriesImportProvider p = (DataSeriesImportProvider)obj;
				providers.add(p);				
			} catch(ClassCastException ex) {
				logger.error("Provider service cannot be cast to correct type.", ex);				
			}
		}
		return providers;
	}

}
