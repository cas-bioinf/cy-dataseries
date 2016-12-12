package cz.cas.mbu.cydataseries;

/**
 * A series with string indices but floating point values.
 * @author Martin
 *
 */
public interface NamedDoubleDataSeries extends DoubleDataSeries<String>, StringIndexDataSeries<Double> {
}
