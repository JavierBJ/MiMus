package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import model.Casa;
import model.Unit;

public class CasaTableViewer extends DeclarativeTableViewer {

	public CasaTableViewer(Composite parent, List<Unit> cases) {
		super(parent);
		this.entities = cases;
		String[] aux = {"Lema", "Titol", "Cort"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE | SWT.READ_ONLY);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setCellModifier(new CasaCellModifier());
		tv.setLabelProvider(new CasaLabelProvider());
		tv.setComparator(new CasaComparator());
	}
	
	class CasaCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			/* 0 is the lemma generated from fields 1 and 2, so it's read-only */
			return getColumnNames().indexOf(property) != 0;
		}

		@Override
		public Object getValue(Object element, String property) {
			Casa casa = (Casa) element;
			int colIdx = getColumnNames().indexOf(property);
			switch (colIdx) {
			case 0:		// Lema		// XXX: necessary when 0 is not modifiable?
				return casa.getLemma();
			case 1:		// Titol
				return casa.getTitol();
			case 2:		// Cort
				return casa.getCort();
			default:	// Shouldn't reach here
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Casa casa = (Casa) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 1:		// Titol
				casa.setTitol((String) value);
				break;
			case 2:		// Cort
				casa.setCort((String) value);
				break;
			default:	// Shouldn't reach here
				break;
			}
		}
	}
	
	class CasaLabelProvider extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Casa casa = (Casa) element;
			switch (columnIndex) {
			case 0:		// Lemma
				return casa.getLemma();
			case 1:		// Titol
				return casa.getTitol();
			case 2:		// Cort
				return casa.getCort();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class CasaComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Casa c1 = (Casa) e1;
			Casa c2 = (Casa) e2;
			return c1.getLemma().compareTo(c2.getLemma());
		}
	}

}
