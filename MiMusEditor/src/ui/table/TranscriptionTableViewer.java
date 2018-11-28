package ui.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import model.Transcription;
import model.TranscriptionsList;
import model.Unit;

public class TranscriptionTableViewer extends MiMusTableViewer {
	
	private TranscriptionsList transcriptions;
	
	public TranscriptionTableViewer(Composite parent) {
		super(parent);
		String[] cols = {"Transcription", "Lemma"};
		columnNames = cols;
		transcriptions = new TranscriptionsList();
	}

	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		tv.setContentProvider(new TranscriptionContentProvider());
		tv.setLabelProvider(new TranscriptionLabelProvider());
		tv.setInput(transcriptions);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new TranscriptionComparator());
		packColumns();
		return tv;
	}
	
	public class TranscriptionContentProvider implements MiMusContentProvider {		
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((TranscriptionsList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((TranscriptionsList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			transcriptions.removeChangeListener(this);
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return transcriptions.getUnits().toArray();
		}
		
		public void addUnit(Unit u) {
			tv.add(u);
		}
		
		public void removeUnit(Unit u) {
			tv.remove(u);
		}
		
		public void updateUnit(Unit u) {
			tv.update(u, null);
			tv.refresh();
		}
	}
	
	class TranscriptionLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Transcription tra = (Transcription) element;
			switch (columnIndex) {
			case 0:	// Transcription
				return tra.getForm();
			case 1:	// Lemma
				return tra.getEntity().getLemma();
			default:	// Should never reach here
				return "";
			}
		}
	}
	
	public class TranscriptionComparator extends ViewerComparator {
		/**
		 * Compares transcriptions alphanumerically, using two criteria:
		 * first, by their lemma; then, ties are resolved by the
		 * transcripted form.
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			Transcription tra1 = (Transcription) e1;
			Transcription tra2 = (Transcription) e2;
			int byLemma = tra1.getEntity().getLemma()
					.compareTo(tra2.getEntity().getLemma());
			if (byLemma==0) {
				return tra1.getForm().compareTo(tra2.getForm());
			} else {
				return byLemma;
			}
		}
	}

	public TranscriptionsList getTranscriptions() {
		return transcriptions;
	}
	public void setTranscriptions(TranscriptionsList transcriptions) {
		this.transcriptions = transcriptions;
	}
}