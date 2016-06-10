package cz.cas.mbu.cytimeseries.internal;

import java.lang.reflect.Constructor;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

import cz.cas.mbu.cytimeseries.DataSeriesException;
import cz.cas.mbu.cytimeseries.DataSeriesManager;

public class ParameterPassingTaskFactory<TASK extends Task> extends AbstractTaskFactory {
	
	private final Object[] parameters;
	private final Class<TASK> taskClass;
	private final Constructor<TASK> constructor;
	
	
	public ParameterPassingTaskFactory(Class<TASK> taskClass, Object ... parameters) {
		super();
		this.taskClass = taskClass;
		this.parameters = parameters;
		
		Constructor<TASK> chosenConstructor = null;
		for(Constructor<?> candidateConstructor : taskClass.getConstructors())
		{
			Class<?> parameterTypes[] = candidateConstructor.getParameterTypes();
			if(parameterTypes.length != parameters.length)
			{
				continue;
			}
			
			boolean paramsOK = true;
			for(int i = 0; i < parameters.length; i++)
			{
				if(!parameterTypes[i].isAssignableFrom(parameters[i].getClass()))
				{
					paramsOK = false;
					break;
				}
			}
			
			if(paramsOK)
			{
				chosenConstructor = (Constructor<TASK>)candidateConstructor;
			}
		}
		
		if(chosenConstructor == null)
		{
			throw new IllegalArgumentException("Could not find any constructor for given parameters.");
		}
		
		constructor = chosenConstructor;
	}


	@Override
	public TaskIterator createTaskIterator() {
		try {
			TASK task = constructor.newInstance(parameters);
			return new TaskIterator(task);		
		} catch (Exception ex)
		{
			throw new DataSeriesException("Could not create task.", ex);
		}
	}

}
