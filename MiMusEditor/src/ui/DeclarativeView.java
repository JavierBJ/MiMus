package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import control.EventObserver;
import control.EventSubject;
import control.SharedControl;
import control.SharedResources;
import util.DBUtils;

public abstract class DeclarativeView extends ViewPart 
		implements EventSubject, EventObserver {
	
	final int LABEL_FLAGS = SWT.VERTICAL;
	final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
	final int COMBO_FLAGS = SWT.DROP_DOWN | SWT.READ_ONLY;
	final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
	
	private Connection conn;
	private SharedResources resources;
	private SharedControl control;
	private List<EventObserver> observers;
	private TableViewer tv;
	protected Button btnAdd;
	protected Button btnClr;
	
	public DeclarativeView() {
		super();
		setObservers(new ArrayList<>());
		setResources(SharedResources.getInstance());
		setControl(SharedControl.getInstance());

		try {
			setConnection(DBUtils.connect());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not load entities from DB.");
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledForm form = initForm(parent);
		developForm(form);
	}
	
	public abstract String getViewName();
	
	private ScrolledForm initForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Declare " + getViewName() + " Entity");
		form.getBody().setLayout(new GridLayout());
		return form;
	}
	
	public abstract void developForm(ScrolledForm form);
	
	public abstract String getAddPattern();
	
	public void addButtons(Composite parent) {
		/* Form buttons */
		btnAdd = new Button(parent, BUTTON_FLAGS);
		btnAdd.setText("Add " + getViewName());
		btnClr = new Button(parent, BUTTON_FLAGS);
		btnClr.setText("Clear fields");
	}
	
	@Override
	public void attach(EventObserver o) {
		getObservers().add(o);
	}
	
	@Override
	public void detach(EventObserver o) {
		getObservers().remove(o);
	}
	
	@Override
	public List<EventObserver> getObservers() {
		return observers;
	}
	
	@Override
	public void setFocus() {}
	
	
	/* Getters and setters */
	public SharedResources getResources() {
		return resources;
	}
	public void setResources(SharedResources resources) {
		this.resources = resources;
	}
	public SharedControl getControl() {
		return control;
	}
	public void setControl(SharedControl control) {
		this.control = control;
	}
	public void setObservers(List<EventObserver> observers) {
		this.observers = observers;
	}
	public TableViewer getTv() {
		return tv;
	}
	public void setTv(TableViewer tv) {
		this.tv = tv;
	}
	public Connection getConnection() {
		return conn;
	}
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
