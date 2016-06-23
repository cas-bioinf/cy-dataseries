package cz.cas.mbu.cydataseries;

@FunctionalInterface
public interface DataSeriesListener {
	void dataSeriesEvent(DataSeriesEvent event);
}
