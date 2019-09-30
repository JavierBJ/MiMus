package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import model.Document;
import model.Entity;
import persistence.DocumentDao;
import util.DBUtils;

/**
 * DeclarativeView is any Eclipse View of MiMus application that allows
 * to declare MiMus Entities. In principle, there is one of these views
 * for every entity declared. All of these must be implemented by
 * inheriting from this class.
 * 
 * DeclarativeViews contain a form that allows to fill the fields that
 * make an Entity of that type. They also contain a table showing all
 * entities of that type, and control buttons.
 * 
 * DeclarativeViews have two functions: to insert new entities and to
 * update existing entities. Hence, sometimes we talk of a state of the
 * view, which can be ADD or EDIT.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <E>
 */
public abstract class DeclarativeView<E extends Entity> extends ViewPart {
	
	/* SWT flags grouped together based on use cases of DeclarativeViews */
	final int LABEL_FLAGS = SWT.VERTICAL;
	final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
	final int COMBO_FLAGS = SWT.DROP_DOWN | SWT.READ_ONLY;
	final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
	
	/* Texts of state */
	final String STATE_ADD = "Adding a new entity";
	final String STATE_EDIT = "Editing an existing entity";
	final String BUTTON_ADD = "Add " + getViewName();
	final String BUTTON_EDIT = "Edit " + getViewName();
	
	private boolean stateAdd;
	private int selectedId;
	private Connection conn;
	private TableViewer tv;
	protected Button btnAdd;
	protected Button btnClr;
	protected Button btnDes;
	protected Label stateLabel;
	protected Text annotationsText;
	
	/**
	 * DeclarativeViews have associated a connection to the DB.
	 */
	public DeclarativeView() {
		super();
		setStateAdd(true);
		setSelectedId(0);
		
		try {
			setConnection(DBUtils.connect());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not load entities from DB.");
		}
	}

	/**
	 * Creates view, consisting of a Refresh button, a form
	 * for introduction of new entities, and a table of
	 * declared entities.
	 */
	@Override
	public void createPartControl(Composite parent) {
		ScrolledForm form = initForm(parent);
		
		Button refreshBtn = new Button(form.getBody(), SWT.PUSH | SWT.CENTER);
		refreshBtn.setText("Refresh");
		
		developForm(form);
		
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					setEntities(retrieveEntities());
				} catch(SQLException e1) {
					e1.printStackTrace();
				}
				
				tv.setInput(getEntities());
				tv.refresh();
				setStateAdd(true);
				setSelectedId(0);
				stateLabel.setText(STATE_ADD);
				btnAdd.setText(BUTTON_ADD);
				
				/* Empty label for document annotations */
				annotationsText.setText("");
			}
		});
	}
	
	/**
	 * Returns the entity name associated to this view. 
	 */
	public abstract String getViewName();
	
	/**
	 * Initializes a ScrolledForm object that facilitates view
	 * creation.
	 */
	private ScrolledForm initForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Declare " + getViewName() + " Entity");
		form.getBody().setLayout(new GridLayout());
		return form;
	}
	
	/**
	 * Implementation of the form for a specific entity must go in
	 * this method.
	 */
	public abstract void developForm(ScrolledForm form);
	
	/**
	 * Draws annotations label in the declarative views.
	 */
	public void addAnnotationsLabel(Composite parent, GridData gd) {
		annotationsText = new Text(parent, 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		annotationsText.setText("");
		annotationsText.setLayoutData(gd);
	}
	
	/**
	 * Draws the label for state in the declarative views.
	 */
	public void addStateLabel(Composite parent) {
		/* On creation, view is in ADD mode */
		stateLabel = new Label(parent, LABEL_FLAGS);
		stateLabel.setText(STATE_ADD);
	}
	
	/**
	 * Draws buttons to add entity, clear form and
	 * deselect entity.
	 */
	public void addButtons(Composite parent) {
		/* Form buttons */
		btnAdd = new Button(parent, BUTTON_FLAGS);
		btnAdd.setText(BUTTON_ADD);	/* On creation, view is in ADD mode */
		btnClr = new Button(parent, BUTTON_FLAGS);
		btnClr.setText("Clear fields");
		btnDes = new Button(parent, BUTTON_FLAGS);
		btnDes.setText("Back to Insert mode");
	}
	
	/**
	 * Contains listeners with actions related to the buttons.
	 */
	public void createTableActions() {
		/* Click deselect button to go back to insert mode */
		btnDes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!stateAdd) {
					tv.getTable().deselectAll();	/* Add mode = nothing selected */
					setStateAdd(true);
					setSelectedId(0);
					stateLabel.setText(STATE_ADD);
					btnAdd.setText(BUTTON_ADD);
					annotationsText.setText("");
				}
			}
		});
		
		/* Select a table row to enter edit mode with the row selected */
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				/* Unchecked warning because selection is Object type */
				Object o = tv.getStructuredSelection().getFirstElement();
				if (o != null) {
					/* User selected a row from the table, move to Edit mode */
					setStateAdd(false);
					setSelectedId(((Entity) o).getSpecificId());	/* Save ID */
					System.out.println("ID is " + getSelectedId());
					stateLabel.setText(STATE_EDIT);
					btnAdd.setText(BUTTON_EDIT);
					fillFieldsFromSelection((E) o);
					
					/* Tell documents where entity is annotated */
					fillAnnotationsLabel((E) o);
				} else {
					/* After an entity is edited, back to add mode */ 
					setStateAdd(true);
					setSelectedId(0);
					stateLabel.setText(STATE_ADD);
					btnAdd.setText(BUTTON_ADD);
					
					/* Empty label for document annotations */
					annotationsText.setText("");
				}
			}
		});
	}
	
	/**
	 * Given an entity of the type of the view, fills the fields of
	 * the form with the attributes of the entity.
	 */
	protected abstract void fillFieldsFromSelection(E ent);
	
	/**
	 * Draws label that says what documents have annotations of a
	 * certain entity.
	 */
	private void fillAnnotationsLabel(E entity) {
		List<Document> docs = new ArrayList<>();
		try {
			docs = new DocumentDao(conn)
					.selectWhereEntity(entity.getId());
		} catch (SQLException e) {
			System.out.println("Could not retrieve documents where entity is.");
			e.printStackTrace();
		}
		String ids = "";
		for (Document doc : docs) {
			ids += doc.getIdStr() + ", ";
		}
		if (ids.length()>2) {
			ids = ids.substring(0, ids.length()-2);
			annotationsText.setText("Used in documents: " + ids);
		} else {
			annotationsText.setText("Used in 0 documents.");
		}
	}
	
	/**
	 * Downloads from DB the list of entities declared in this view.
	 */
	public abstract List<E> retrieveEntities() throws SQLException;
	
	/**
	 * Returns the list of entities declared in this view, which is
	 * not necessarily updated wrt the DB.
	 */
	public abstract List<E> getEntities();
	
	/**
	 * Updates the list of entities declared in this view.
	 */
	public abstract void setEntities(List<E> entities);
	
	/**
	 * Given the table in the view, selects the row corresponding
	 * to Entity <ent>.
	 */
	public void selectEntityInTable(Entity ent) {
		for (int i=0; i<getEntities().size(); i++) {
			Entity thisEnt = (Entity) getTv().getElementAt(i);
			if (thisEnt.getId() == ent.getId()) {
				getTv().getTable().select(i);
				break;
			}
		}
	}
	
	@Override
	public void setFocus() {}
	
	/* Getters and setters */
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
	public boolean isStateAdd() {
		return stateAdd;
	}
	public void setStateAdd(boolean state) {
		this.stateAdd = state;
	}
	public int getSelectedId() {
		return selectedId;
	}
	public void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
	}
}
