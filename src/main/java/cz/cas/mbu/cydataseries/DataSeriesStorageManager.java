package cz.cas.mbu.cydataseries;

public interface DataSeriesStorageManager {
	DataSeriesStorageProvider getStorageProvider(Class<?> seriesClass);
}
