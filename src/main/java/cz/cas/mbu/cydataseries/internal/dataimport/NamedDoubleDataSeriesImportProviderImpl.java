package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.List;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.NamedDoubleDataSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.data.NamedDoubleDataSeriesImpl;

/**
 * Import provider for {@link NamedDoubleDataSeriesImpl}.
 * @author MBU
 *
 */
public class NamedDoubleDataSeriesImportProviderImpl implements DataSeriesImportProvider{

	@Override
	public DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults) {
		preImportResults.checkConsistentcy();
		
		int[] rowIDs = new int[preImportResults.getCellData().length];
		for(int i = 0; i < rowIDs.length; i++)
		{
			rowIDs[i] = i;
		}
		List<String> rowNames = preImportResults.getRowNames();
		
		List<String> indexArray = new ArrayList<>(preImportResults.getIndexValues());
					
		double[][] dataArray = ImportProviderUtils.parseDataArrayAsDoubles(preImportResults);

		return new NamedDoubleDataSeriesImpl(suid, name, rowIDs, rowNames, indexArray, dataArray);
	}

	@Override
	public String getDescription() {
		return "Named numeric series";
	}

	@Override
	public Class<? extends DataSeries<?, ?>> getImportedClass() {
		return NamedDoubleDataSeries.class;
	}
	
	

}
