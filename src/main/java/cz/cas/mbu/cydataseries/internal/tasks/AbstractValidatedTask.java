package cz.cas.mbu.cydataseries.internal.tasks;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TunableValidator;

public abstract class AbstractValidatedTask extends AbstractTask implements TunableValidator {

	protected abstract ValidationState getValidationState(StringBuilder messageBuilder);

	@Override
	public final ValidationState getValidationState(Appendable errMsg) {
		StringBuilder messageBuilder = new StringBuilder();
		ValidationState returnValue = getValidationState(messageBuilder);
		try {
			errMsg.append(messageBuilder.toString());
		}
		catch(IOException ex) {
			Logger.getLogger(CyUserLog.NAME).error("Could not append validation message", ex); 
			Logger.getLogger(CyUserLog.NAME).error("The validation message was: " + messageBuilder.toString(), ex); 
		}
		return returnValue;
	}

}
