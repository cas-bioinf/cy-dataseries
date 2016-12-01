package cz.cas.mbu.cydataseries.internal.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CheckBoxList extends JList<CheckBoxList.Item> implements ItemSelectable {
	
	private final List<ItemListener> itemListeners = new ArrayList<>();
	
	public static class Item {
		private final String label;
		private boolean selected;

		public Item(String label) {
			super();
			this.label = label;
		}

		public Item(String label, boolean selected) {
			super();
			this.selected = selected;
			this.label = label;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean checked) {
			this.selected = checked;
		}

		public String getLabel() {
			return label;
		}

	}

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private void init() {
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					Item item = getModel().getElementAt(index);
					item.setSelected(!item.isSelected());
					fireItemEvent(new ItemEvent(CheckBoxList.this, index, item, item.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
					repaint();
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public CheckBoxList() {
		init();
	}

	public CheckBoxList(Item[] listData) {
		super(listData);
		init();
	}

	public CheckBoxList(ListModel<Item> dataModel) {
		super(dataModel);
		init();
	}

	public CheckBoxList(Vector<? extends Item> listData) {
		super(listData);
		init();
	}

	@Override
	public void addItemListener(ItemListener listener)
	{
		itemListeners.add(listener);
	}	

	@Override
	public void removeItemListener(ItemListener l) {
		itemListeners.remove(l);
	}
	
	@Override
	public Object[] getSelectedObjects() {
		List<Item> selectedItems = new ArrayList<>();
		for(int i = 0; i < getModel().getSize(); i++)
		{
			Item item = getModel().getElementAt(i);
			if(item.isSelected()) {
				selectedItems.add(item);
			}
		}
		
		return selectedItems.toArray();
	}
	

	protected void fireItemEvent(ItemEvent evt)
	{
		itemListeners.forEach(x -> x.itemStateChanged(evt));
	}
	
	protected class CellRenderer implements ListCellRenderer<Item> {
		private JCheckBox checkbox;

		public CellRenderer() {
			checkbox = new JCheckBox();
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Item> list, Item value, int index,
				boolean isSelected, boolean cellHasFocus) {
			checkbox.setSelected(value.isSelected());
			checkbox.setText(value.getLabel());

			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}
	}
}