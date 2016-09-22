package cz.cas.mbu.cydataseries.internal.ui;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.ChartColor;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;

public class ChartUtils {
	public static DrawingSupplier createDrawingSupplier()
	{
		//Our paint sequence (the default contains very light yellow, which is not legible
		Paint[] paintSequence = new Paint[] {
	            new Color(0xFF, 0x55, 0x55),
	            new Color(0x55, 0x55, 0xFF),
	            new Color(0x55, 0xFF, 0x55),
	            new Color(0xFF, 0x55, 0xFF),
	            new Color(0x55, 0xFF, 0xFF),
	            Color.pink,
	            Color.gray,
	            ChartColor.DARK_RED,
	            ChartColor.DARK_BLUE,
	            ChartColor.DARK_GREEN,
	            ChartColor.DARK_YELLOW,
	            ChartColor.DARK_MAGENTA,
	            ChartColor.DARK_CYAN,
	            Color.darkGray,
	            ChartColor.LIGHT_RED,
	            ChartColor.LIGHT_BLUE,
	            ChartColor.LIGHT_GREEN,
	            ChartColor.LIGHT_YELLOW,
	            ChartColor.LIGHT_MAGENTA,
	            ChartColor.LIGHT_CYAN,
	            Color.lightGray,
	            ChartColor.VERY_DARK_RED,
	            ChartColor.VERY_DARK_BLUE,
	            ChartColor.VERY_DARK_GREEN,
	            ChartColor.VERY_DARK_YELLOW,
	            ChartColor.VERY_DARK_MAGENTA,
	            ChartColor.VERY_DARK_CYAN,
	            ChartColor.VERY_LIGHT_RED,
	            ChartColor.VERY_LIGHT_BLUE,
	            ChartColor.VERY_LIGHT_GREEN,
	            ChartColor.VERY_LIGHT_YELLOW,
	            ChartColor.VERY_LIGHT_MAGENTA,
	            ChartColor.VERY_LIGHT_CYAN
	        };		
		
		//Replace the drawing supplier with a supplier with all defaults, but a different paint sequence
		return new DefaultDrawingSupplier(paintSequence,
				DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, 
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);

		
	}		
	
}
