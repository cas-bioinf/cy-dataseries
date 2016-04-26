package cz.cas.mbu.cytimeseries;

public class TimeSeriesException extends RuntimeException {

	public TimeSeriesException() {
		super();
	}

	public TimeSeriesException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeSeriesException(String message) {
		super(message);
	}

	public TimeSeriesException(Throwable cause) {
		super(cause);
	}
 
}
