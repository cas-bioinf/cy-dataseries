package cz.cas.mbu.cydataseries.internal.dataimport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.GUITunableHandlerFactory;

public class SoftFileImportParametersGUIHandlerFactory implements GUITunableHandlerFactory<SoftFileImportParametersGUIHandler> {

		@Override
		public SoftFileImportParametersGUIHandler createTunableHandler(Field field, Object instance, Tunable t) {
			if (!SoftFileImportParameters.class.isAssignableFrom(field.getType()))
				return null;

			return new SoftFileImportParametersGUIHandler(field, instance, t);
		}

		@Override
		public SoftFileImportParametersGUIHandler createTunableHandler(Method getter, Method setter, Object instance, Tunable tunable) {
			if (!SoftFileImportParameters.class.isAssignableFrom(getter.getReturnType()))
				return null;

			return new SoftFileImportParametersGUIHandler(getter, setter, instance, tunable);
		}

}
