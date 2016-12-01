package cz.cas.mbu.cydataseries.internal.dataimport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.GUITunableHandlerFactory;

public class TabularFilesImportParametersGuiHandlerFactory implements GUITunableHandlerFactory<TabularFileImportParametersGUIHandler> {

		@Override
		public TabularFileImportParametersGUIHandler createTunableHandler(Field field, Object instance, Tunable t) {
			if (!TabularFileImportParameters.class.isAssignableFrom(field.getType()))
				return null;

			return new TabularFileImportParametersGUIHandler(field, instance, t);
		}

		@Override
		public TabularFileImportParametersGUIHandler createTunableHandler(Method getter, Method setter, Object instance, Tunable tunable) {
			if (!TabularFileImportParameters.class.isAssignableFrom(getter.getReturnType()))
				return null;

			return new TabularFileImportParametersGUIHandler(getter, setter, instance, tunable);
		}

}
