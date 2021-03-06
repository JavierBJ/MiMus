package ui.dialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import model.Unit;

/**
 * EditorDialog is an Eclipse Dialog that allows users to select
 * a unit to be inserted to DB. Different types of Units require
 * inheriting from this class.
 * 
 * After the user selected an element and the Dialog is closed,
 * the Editor can access the selection made by the user, in order
 * to perform the insertion. That is, the Dialog only contains the
 * user selection procedure.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <U> the type of Unit being added by the Dialog
 */
public abstract class EditorDialog<U extends Unit> extends Dialog {

	/* Model attributes */
	private List<U> units;
	private int selection;
	private U unit;
	
	/* UI attributes */
	private ScrolledForm form;
	private Label label;
	
	protected EditorDialog(List<U> units, Shell parentShell) {
		super(parentShell);
		this.units = units;
		this.selection = 0;
		this.unit = null;
		
		this.form = null;
		this.label = null;
		
		/* Dialog window will block Editor until closed */
		this.setBlockOnOpen(true);
	}
	
	/**
	 * Draws the Dialog, which contains a ComboBox with the elements
	 * available for insertion and control buttons. This component has
	 * autocomplete functionality: if the user starts writing, they
	 * will find the options available starting by this text.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		form = toolkit.createScrolledForm(composite);
		form.setText("Select " + getDialogName());
		form.getBody().setLayout(new GridLayout());
		
		/* 
		 * Create combo with artist string representations as values.
		 * Combo has autocomplete properties when user inputs text.
		 */
		List<U> units = getUnitsUsed();
		Combo combo = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		String[] artistNames = new String[units.size()];
		for (int i=0; i<units.size(); i++) {
			artistNames[i] = units.get(i).toString();
		}
		combo.setItems(artistNames);
		combo.select(0);
		new AutoCompleteField(combo, new ComboContentAdapter(), combo.getItems());
		
		/* Initialize stored values */
		this.setSelection(0);
		if (units.size()==0) {
			this.setUnit(null);
		} else {
			this.setUnit(units.get(0));
		}
		
		/* 
		 * Listeners for combo selection. Two are needed:
		 * 1. Modify Listener for the autocomplete. It does a binary search everytime
		 * an input modification happens. It should be tested how that behaves when
		 * the list of entities grows.
		 * 2. Selection Listener for the classic drop-down menu selection.
		 * Both ways store the selection in the class attributes that can be accessed
		 * externally.
		 */
		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int idx = Arrays.binarySearch(artistNames, combo.getText());
				if (idx>=0) {
					EditorDialog.this.setSelection(idx);
					EditorDialog.this.setUnit(units.get(getSelection()));
				}
			}});
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EditorDialog.this.setSelection(combo.getSelectionIndex());
				EditorDialog.this.setUnit(units.get(getSelection()));
			}
		});
		
		label = new Label(form.getBody(), SWT.VERTICAL);
		label.setText("");
		
		return composite;
	}
	
	/**
	 * Returns the name of the Dialog for presentation of titles.
	 */
	public abstract String getDialogName();

	public List<U> getUnits() {
		return units;
	}

	public void setUnits(List<U> units) {
		this.units = units;
	}
	
	/**
	 * Given the list of units passed to the Dialog, this method
	 * returns a sublist of the previous, which only contains the
	 * units actually available in the Dialog. This provides
	 * flexibility, as it allows to do filtering of the units
	 * passed by the Editor depending on the type of Dialog implemented.
	 */
	public abstract List<U> getUnitsUsed();
	
	public int getSelection() {
		return selection;
	}
	
	public void setSelection(int selection) {
		this.selection = selection;
	}
	
	public U getUnit() {
		return unit;
	}
	
	public void setUnit(U unit) {
		this.unit = unit;
	}
	
	public ScrolledForm getForm() {
		return form;
	}

	public void setForm(ScrolledForm form) {
		this.form = form;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
}
