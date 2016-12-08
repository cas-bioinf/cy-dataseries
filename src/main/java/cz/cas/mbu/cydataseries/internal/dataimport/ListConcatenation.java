package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.AbstractList;
import java.util.List;

public class ListConcatenation<T> extends AbstractList<T> {
	private final List<T> list1;
	private final List<T> list2;
	
	public ListConcatenation(List<T> list1, List<T> list2) {
		super();
		if(list1 == null || list2 == null)
		{
			throw new NullPointerException("No list can be null.");
		}
		this.list1 = list1;
		this.list2 = list2;
	}

	@Override
	public T get(int index) {
		int list1Size = list1.size();
		if(index < list1Size)
		{
			return list1.get(index);
		}
		else if(index < list1Size + list2.size())
		{
			return list2.get(index - list1Size);
		}
		throw new IndexOutOfBoundsException("Index " + index + ", size: " + size());
	}

	@Override
	public int size() {	
		return list1.size() + list2.size();
	}
	
	
	
}
