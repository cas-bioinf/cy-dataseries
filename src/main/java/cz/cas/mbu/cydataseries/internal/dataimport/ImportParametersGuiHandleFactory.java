package cz.cas.mbu.cydataseries.internal.dataimport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.GUITunableHandlerFactory;

public class ImportParametersGuiHandleFactory implements GUITunableHandlerFactory<ImportParametersGUIHandler> {

	    
		@Override
		public ImportParametersGUIHandler createTunableHandler(Field field, Object instance, Tunable t) {
			if (!ImportParameters.class.isAssignableFrom(field.getType()))
				return null;

			return new ImportParametersGUIHandler(field, instance, t);
		}

		@Override
		public ImportParametersGUIHandler createTunableHandler(Method getter, Method setter, Object instance, Tunable tunable) {
			if (!ImportParameters.class.isAssignableFrom(getter.getReturnType()))
				return null;

			return new ImportParametersGUIHandler(getter, setter, instance, tunable);
		}

}
