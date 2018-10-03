package editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.EntitiesList;
import model.Entity;
import model.Lemma;
import model.LemmasList;
import model.MiMusEntry;
import model.MiMusEntryReader;
import model.MiMusFormatException;
import model.Relation;
import model.RelationsList;
import ui.TextStyler;

public class Editor extends EditorPart {

	public static final String[] ENTITY_TYPES = {"Person", "Payment", "Place"};
	public static final String[] PERSON_TYPES = {"Trobadour", "King"};
	public static final String[] PLACE_TYPES = {"Town", "Country"};
	public static final String[] RELATION_TYPES = {"Same as", "Pays", "Goes"};
	
	protected String txtPath;
	protected String xmlPath;
	private boolean hasXML;
	private MiMusEntry docEntry;
	private String[] regestWords;
	private String[] transcriptionWords;
	private StyledText regestText;
	private StyledText transcriptionText;
	private String docID;
	private int entityCurrentID;
	
	public Editor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		docID = getEditorInput().getName().substring(0, getEditorInput().getName().indexOf('.'));
		System.out.println("Doc ID: " + docID);
		entityCurrentID = 0;
		
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder txtFolder = project.getFolder("txt");
		IFolder xmlFolder = project.getFolder("xml");
		txtPath = txtFolder.getLocation().toString();
		xmlPath = xmlFolder.getLocation().toString();
		
		if (getEditorInput().getName().endsWith(".txt")) {
			txtPath += "/" + getEditorInput().getName();
			xmlPath += "/" + getEditorInput().getName().replace(".txt", ".xml");
			File xmlFile = new File(xmlPath);
			hasXML = xmlFile.exists();
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else if (getEditorInput().getName().endsWith(".xml")) {
			xmlPath += "/" + getEditorInput().getName();
			txtPath += "/" + getEditorInput().getName().replace(".xml", ".txt");
			hasXML = true;
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else {
			System.out.println("Error: incompatible input file.");
			System.exit(1);
		}
		
		/* Txt must always be present so the text can be loaded */
		try {
			docEntry = new MiMusEntryReader().read(txtPath);
			regestWords = docEntry.getRegest().split(" ");
			transcriptionWords = docEntry.getTranscription().split(" ");
		} catch (MiMusFormatException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Annotation");
		form.getBody().setLayout(new GridLayout());
		
		/* Regest text */
		regestText = new StyledText(form.getBody(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		regestText.setText(docEntry.getRegest());
		regestText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
		regestText.setEditable(false);
		TextStyler styler = new TextStyler(regestText);
		
		/* List of entities */
		Section sectEnt = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectEnt.setText("Entities");
		
		/* Table of entities */
		EntityTableViewer entityHelper = new EntityTableViewer(sectEnt.getParent(), styler, regestWords);
		TableViewer entityTV = entityHelper.createTableViewer();
		EntitiesList regestEntities = entityHelper.getEntities();
		
		/* Buttons to add/remove entities */
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		Button setEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		setEnt.setLayoutData(gridData);
		setEnt.setText("Add Entity from Text Selection");
		
		Button removeEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		removeEnt.setLayoutData(gridData);
		removeEnt.setText("Delete");
		
		/* List of relations */
		Section sectRel = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectRel.setText("Relations");
		
		/* Table of relations */
		RelationTableViewer relationHelper = new RelationTableViewer(sectRel.getParent(), styler, regestEntities);
		TableViewer relationTV = relationHelper.createTableViewer();
		RelationsList relations = relationHelper.getRelations();
		
		/* Buttons to add/remove relations */
		GridData gridRel = new GridData();
		gridRel.widthHint = 300;
		Button addRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
		addRel.setLayoutData(gridRel);
		addRel.setText("Add");
		
		Button removeRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
		removeRel.setLayoutData(gridRel);
		removeRel.setText("Delete");
		
		Label info = toolkit.createLabel(form.getBody(), "");
		info.setForeground(new Color(Display.getCurrent(), 255,0,0));
		info.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setEnt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Point charCoords = regestText.getSelection();
				if (charCoords.x!=charCoords.y) {
					charCoords = fromWordToCharCoordinates(
							fromCharToWordCoordinates(
							charCoords, docEntry.getRegest()), regestWords);	// Trick to ensure selection of whole words
					Point wordCoords = fromCharToWordCoordinates(charCoords, docEntry.getRegest());
					regestEntities.addUnit(new Entity(regestWords, wordCoords.x, wordCoords.y, entityCurrentID++));
					//entityHelper.packColumns();
					System.out.println("Adding Selected Entity - " + regestEntities.countUnits());
					printAddedInfo(info);
					styler.addUpdate(charCoords.x, charCoords.y);
				} else {
					System.out.println("Could not add Selected Entity because no text was selected");
					printNotAddedInfo(info);
				}
			}
		});
		removeEnt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Entity ent = (Entity) ((IStructuredSelection) entityTV.getSelection())
						.getFirstElement();
				if (ent==null) {
					System.out.println("Could not remove Entity because none was selected.");
					printNotDeletedBecauseNullInfo(info);
				} else if (relations.using(ent)) {
					System.out.println("Could not remove Entity because it is used in an existing Relation.");
					printNotDeletedBecauseUsedInfo(info);
				} else {
					System.out.println(ent);
					Point charCoords = fromWordToCharCoordinates(
							new Point(ent.getFrom(), ent.getTo()), regestWords);
					regestEntities.removeUnit(ent);
					entityHelper.packColumns();
					System.out.println("Removing entity - " + regestEntities.countUnits());
					printDeletedInfo(info);
					styler.deleteUpdate(charCoords.x, charCoords.y);
				}
			}
		});
		addRel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (regestEntities.countUnits()<2) {
					printRelationNotAddedInfo(info);
					System.out.println("Cannot create relation with fewer than 2 entities.");
				} else {
					relations.addUnit(new Relation(regestEntities, 0, 1));
					printRelationAddedInfo(info);
					System.out.println("Adding relation - " + relations.countUnits());
				}
				
			}
		});
		removeRel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Relation rel = (Relation) ((IStructuredSelection) relationTV.getSelection())
						.getFirstElement();
				if (rel==null) {
					System.out.println("Could not remove Relation because none was selected.");
					printRelationNotDeletedInfo(info);
				} else {
					System.out.println(rel);
					relations.removeUnit(rel);
					printRelationDeletedInfo(info);
					System.out.println("Removing relation - " + relations.countUnits());
				}
			}
		});
		
		/* Transcription part of the form */
		Section sectTrans = new Section(form.getBody(), PROP_TITLE);
		sectTrans.setText("Transcription of the document");
		
		/* Transcription text */
		transcriptionText = new StyledText(sectTrans.getParent(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		transcriptionText.setText(docEntry.getTranscription());
		transcriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
		transcriptionText.setEditable(false);
		TextStyler transcriptionStyler = new TextStyler(transcriptionText);
		EntitiesList transcriptionEntities = new EntitiesList(transcriptionWords);

		LemmaTableViewer lemmaHelper = new LemmaTableViewer(sectTrans.getParent(), transcriptionStyler, regestEntities, transcriptionEntities);
		TableViewer lemmaTV = lemmaHelper.createTableViewer();
		LemmasList lemmas = lemmaHelper.getLemmas();
		
		/* Buttons to add/remove lemma associations */
		GridData gridLemma = new GridData();
		gridLemma.widthHint = 300;
		Button addLemma = new Button(sectTrans.getParent(), SWT.PUSH | SWT.CENTER);
		addLemma.setLayoutData(gridLemma);
		addLemma.setText("Add");
		
		Button removeLemma = new Button(sectTrans.getParent(), SWT.PUSH | SWT.CENTER);
		removeLemma.setLayoutData(gridLemma);
		removeLemma.setText("Delete");
		
		Label infoTrans = toolkit.createLabel(sectTrans.getParent(), "");
		infoTrans.setForeground(new Color(Display.getCurrent(), 255,0,0));
		infoTrans.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		addLemma.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point charCoords = transcriptionText.getSelection();
				if (charCoords.x!=charCoords.y) {
					charCoords = fromWordToCharCoordinates(
							fromCharToWordCoordinates(
							charCoords, docEntry.getTranscription()), transcriptionWords);	// Trick to ensure selection of whole words
					Point wordCoords = fromCharToWordCoordinates(charCoords, docEntry.getTranscription());
					Entity transEnt = new Entity(transcriptionWords, wordCoords.x, wordCoords.y, entityCurrentID++);
					transcriptionEntities.addUnit(transEnt);
					lemmas.addUnit(new Lemma(regestEntities, transcriptionEntities, 0, transcriptionEntities.countUnits()-1));
					printLemmaAddedInfo(infoTrans);
					transcriptionStyler.addUpdate(charCoords.x, charCoords.y);
				} else {
					System.out.println("Could not add Selected Entity because no text was selected");
					printLemmaNotAddedInfo(infoTrans);
				}
			}
		});
		removeLemma.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lemma lemma = (Lemma) ((IStructuredSelection) lemmaTV.getSelection())
							.getFirstElement();
				if (lemma==null) {
					System.out.println("Could not remove Lemma because none was selected.");
					printLemmaNotDeletedInfo(infoTrans);
				} else {
					System.out.println(lemma);
					lemmas.removeUnit(lemma);
					System.out.println("Removing lemma - " + lemmas.countUnits());
					printLemmaDeletedInfo(infoTrans);
					Point charCoords = fromWordToCharCoordinates(new Point(
							lemma.getTranscriptionEntityObject().getFrom(), 
							lemma.getTranscriptionEntityObject().getTo()), transcriptionWords);
					transcriptionStyler.deleteUpdate(charCoords.x, charCoords.y);
				}
			}
		});
		
		/* XML Button */
		Section sectXML = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectXML.setText("Create XML");
		Button btn = new Button(sectXML.getParent(), SWT.PUSH);
		btn.setText("Press to save as XML");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// TODO: move construction of Document out of here
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = docBuilder.newDocument();
					
					Element tagDocument = doc.createElement("document");
					doc.appendChild(tagDocument);
					
					Element tagID = doc.createElement("doc_id");
					tagID.appendChild(doc.createTextNode(docID));
					tagDocument.appendChild(tagID);
					
					Element tagRegest = doc.createElement("regest");
					tagRegest.appendChild(doc.createTextNode(docEntry.getRegest()));
					tagDocument.appendChild(tagRegest);
					
					Element tagTranscription = doc.createElement("transcription");
					tagTranscription.appendChild(doc.createTextNode(docEntry.getTranscription()));
					tagDocument.appendChild(tagTranscription);
					
					/* Regest entities */
					Element tagRegestEntities = doc.createElement("regest_entities");
					tagDocument.appendChild(tagRegestEntities);
					for (Entity ent: regestEntities.getUnits()) {
						Element tagIDEnt = doc.createElement("entity_id");
						tagIDEnt.appendChild(doc.createTextNode(String.valueOf(ent.getId())));
						Element tagFromEnt = doc.createElement("from");
						tagFromEnt.appendChild(doc.createTextNode(String.valueOf(ent.getFrom())));
						Element tagToEnt = doc.createElement("to");
						tagToEnt.appendChild(doc.createTextNode(String.valueOf(ent.getTo())));
						Element tagTextEnt = doc.createElement("text");
						tagTextEnt.appendChild(doc.createTextNode(ent.getText()));
						Element tagTypeEnt = doc.createElement("type");
						tagTypeEnt.appendChild(doc.createTextNode(ent.getTypeWord()));
						Element tagSubtypeEnt = doc.createElement("subtype");
						tagSubtypeEnt.appendChild(doc.createTextNode(ent.getSubtypeWord()));
						Element tagEntity = doc.createElement("entity");
						tagEntity.appendChild(tagIDEnt);
						tagEntity.appendChild(tagFromEnt);
						tagEntity.appendChild(tagToEnt);
						tagEntity.appendChild(tagTextEnt);
						tagEntity.appendChild(tagTypeEnt);
						tagEntity.appendChild(tagSubtypeEnt);
						tagRegestEntities.appendChild(tagEntity);
					}
					
					/* Transcription entities */
					Element tagTranscriptionEntities = doc.createElement("transcription_entities");
					tagDocument.appendChild(tagTranscriptionEntities);
					for (Entity ent: transcriptionEntities.getUnits()) {
						Element tagIDEnt = doc.createElement("entity_id");
						tagIDEnt.appendChild(doc.createTextNode(String.valueOf(ent.getId())));
						Element tagFromEnt = doc.createElement("from");
						tagFromEnt.appendChild(doc.createTextNode(String.valueOf(ent.getFrom())));
						Element tagToEnt = doc.createElement("to");
						tagToEnt.appendChild(doc.createTextNode(String.valueOf(ent.getTo())));
						Element tagTextEnt = doc.createElement("text");
						tagTextEnt.appendChild(doc.createTextNode(ent.getText()));
						Element tagTypeEnt = doc.createElement("type");
						tagTypeEnt.appendChild(doc.createTextNode(ent.getTypeWord()));
						Element tagSubtypeEnt = doc.createElement("subtype");
						tagSubtypeEnt.appendChild(doc.createTextNode(ent.getSubtypeWord()));
						Element tagEntity = doc.createElement("entity");
						tagEntity.appendChild(tagIDEnt);
						tagEntity.appendChild(tagFromEnt);
						tagEntity.appendChild(tagToEnt);
						tagEntity.appendChild(tagTextEnt);
						tagEntity.appendChild(tagTypeEnt);
						tagEntity.appendChild(tagSubtypeEnt);
						tagTranscriptionEntities.appendChild(tagEntity);
					}
					
					/* Lemmatizations */
					Element tagLemmatizations = doc.createElement("lemmatizations");
					tagDocument.appendChild(tagLemmatizations);
					for (Relation r: lemmas.getUnits()) {
						Lemma lem = (Lemma) r;
						Element tagTranscriptionId = doc.createElement("transcription_id");
						tagTranscriptionId.appendChild(doc.createTextNode(String.valueOf(lem.getTranscriptionEntityObject().getId())));
						Element tagRegestId = doc.createElement("regest_id");
						tagRegestId.appendChild(doc.createTextNode(String.valueOf(lem.getRegestEntityObject().getId())));
						Element tagLemmatization = doc.createElement("lemmatization");
						tagLemmatization.appendChild(tagTranscriptionId);
						tagLemmatization.appendChild(tagRegestId);
						tagLemmatizations.appendChild(tagLemmatization);
					}
					
					/* Converts Java XML Document to file-system XML */
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
			        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			        DOMSource source = new DOMSource(doc);
			        
			        File f = new File(xmlPath);
			        StreamResult console = new StreamResult(f);
			        transformer.transform(source, console);
			        
			        printSavedInfo(info);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		toolkit.dispose();
		
		/* Load entities that were declared in the XML */
		if (hasXML) {
			try {
				File xmlFile = new File(xmlPath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();
				
				/* Load entities */
				docID = doc.getElementsByTagName("doc_id").item(0).getTextContent();
				NodeList nl = doc.getElementsByTagName("entity");
				for (int i=0; i<nl.getLength(); i++) {
					Node nEnt = nl.item(i);
					if (nEnt.getNodeType() == Node.ELEMENT_NODE) {
						/* Read fields from XML entry <entity> */
						Element eEnt = (Element) nEnt;
						
						/* Discern if <entity> under <regest_entities> or <transcription_entities> */
						if (nEnt.getParentNode().getNodeName().equals("regest_entities")) {
							Entity ent = xmlElementToEntity(eEnt, regestWords);
							entityCurrentID++;
							regestEntities.addUnit(ent);
							Point charCoord = fromWordToCharCoordinates(new Point(ent.getFrom(), ent.getTo()), regestWords);
							styler.add(charCoord.x, charCoord.y);
						} else if (nEnt.getParentNode().getNodeName().equals("transcription_entities")) {
							Entity ent = xmlElementToEntity(eEnt, transcriptionWords);
							entityCurrentID++;
							transcriptionEntities.addUnit(ent);
							Point charCoord = fromWordToCharCoordinates(new Point(ent.getFrom(), ent.getTo()), transcriptionWords);
							transcriptionStyler.add(charCoord.x, charCoord.y);
						}					
					}
				}
				styler.update();
				transcriptionStyler.update();
				entityHelper.packColumns();
				
				/* Load lemmatizations */
				nl = doc.getElementsByTagName("lemmatization");
				for (int i=0; i<nl.getLength(); i++) {
					Node nLem = nl.item(i);
					if (nLem.getNodeType() == Node.ELEMENT_NODE) {
						Element eLem = (Element) nLem;
						String strTranscriptionID = eLem.getElementsByTagName("transcription_id").item(0).getTextContent();
						String strRegestID = eLem.getElementsByTagName("regest_id").item(0).getTextContent();
						Entity foundTranscriptionEntity = null;
						Entity foundRegestEntity = null;
						
						/* Find entities whose IDs coincide with the IDs stored in the <lemmatization> entry */
						NodeList entNL = doc.getElementsByTagName("entity");
						for (int j=0; (foundTranscriptionEntity==null || foundRegestEntity==null) 
								&& j<entNL.getLength(); j++) {
							Node nEnt = entNL.item(j);
							if (nEnt.getNodeType() == Node.ELEMENT_NODE) {
								Element eEnt = (Element) nEnt;
								
								/* Discern if <entity> entries retrieved are under <transcription_entities> or <regest_entities> */
								if (nEnt.getParentNode().getNodeName().equals("transcription_entities")
										&& eEnt.getElementsByTagName("entity_id").item(0).getTextContent().equals(strTranscriptionID)) {
									foundTranscriptionEntity = xmlElementToEntity(eEnt, transcriptionWords);
								} else if (nEnt.getParentNode().getNodeName().equals("regest_entities")
										&& eEnt.getElementsByTagName("entity_id").item(0).getTextContent().equals(strRegestID)) {
									foundRegestEntity = xmlElementToEntity(eEnt, regestWords);
								}
							}
						}
						
						/* Find index of the entities retrieved in their corresponding EntitiesList */
						int idxRegestEnt = regestEntities.getUnits().indexOf(foundRegestEntity);
						int idxTranscriptionEnt = transcriptionEntities.getUnits().indexOf(foundTranscriptionEntity);
						lemmas.addUnit(new Lemma(regestEntities, transcriptionEntities, idxRegestEnt, idxTranscriptionEnt));
					}
				}
			} catch (ParserConfigurationException pce) {
				System.out.println("Error with DOM parser.");
				pce.printStackTrace();
			} catch (IOException | SAXException ioe) {
				System.out.println("Error parsing document " + xmlPath);
				ioe.printStackTrace();
			}
		}
	}

	// TODO: Rethink these methods so they are inherent of a textual object
	
	private Entity xmlElementToEntity(Element elem, String[] words) {
		int from = Integer.parseInt(elem.getElementsByTagName("from").item(0).getTextContent());
		int to = Integer.parseInt(elem.getElementsByTagName("to").item(0).getTextContent());
		String type = elem.getElementsByTagName("type").item(0).getTextContent();
		String subtype = elem.getElementsByTagName("subtype").item(0).getTextContent();
		int id = Integer.parseInt(elem.getElementsByTagName("entity_id").item(0).getTextContent());
		return new Entity(words, from, to, type, subtype, id);
	}
	
	private Point fromCharToWordCoordinates(Point old, String text) {
		List<Integer> spaces = getSpacesInText(text);
		/*
		 * Looks for between which spaces our selection indexes fall,
		 * which corresponds with a certain word whose index is returned.
		 */
		int from = 0;
		int to = 0;
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.x>=spaces.get(i) && old.x<spaces.get(i+1)) {
				from = i;
				break;
			}
		}
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.y-1>=spaces.get(i) && old.y-1<=spaces.get(i+1)) {
				to = i;
				break;
			}
		}
		return new Point(from, to);
	}
	
	private Point fromWordToCharCoordinates(Point old, String[] words) {
		int charIdx=0;
		int wordIdx=0;
		while (wordIdx++<old.x) {	// Advance charIdx until start of first word
			charIdx += words[wordIdx-1].length() + 1;	// +1 for the space
		}
		int newX = new Integer(charIdx);	// Fix start of first word
		charIdx += words[wordIdx-1].length() + 1;	// Advance first word
		while (wordIdx++<old.y) {	// Advance charIdx until start of last word
			charIdx += words[wordIdx-1].length() + 1;
		}
		
		if (old.y-old.x>0) {
			int newY = charIdx + words[wordIdx-1].length();	// Advance charIdx until end of last word
			return new Point(newX, newY);
		} else {
			return new Point(newX, charIdx-1);	// In this case we already advanced before
		}	
	}
	
	private List<Integer> getSpacesInText(String text) {
		/* 
		 * Spaces contains the index of every space, besides the start
		 * and ending index of the full text, in ascending order.
		 */
		List<Integer> spaces = new ArrayList<>();
		spaces.add(0);
		for (int idxSpace = text.indexOf(' '); 
				idxSpace>=0;
				idxSpace = text.indexOf(' ', idxSpace+1)) {
			spaces.add(idxSpace);
		}
		spaces.add(text.length());
		return spaces;
	}
	
	private void printAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Entity added successfully.");
	}
	
	private void printNotAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a part of text.");
	}
	
	private void printDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Entity deleted successfully.");
	}
	
	private void printNotDeletedBecauseNullInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select an entity to delete it.");
	}
	
	private void printNotDeletedBecauseUsedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must remove all relations where this entity is used before you can remove it.");
	}
	
	private void printSavedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Saved to XML file successfully.");
	}
	
	private void printRelationAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Relation created successfully.");
	}
	
	private void printRelationNotAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("Cannot create a relation if fewer than 2 entities are created.");
	}
	
	private void printRelationDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Relation deleted successfully.");
	}
	
	private void printRelationNotDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a relation to delete it.");
	}
	
	private void printLemmaAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Lemma added successfully.");
	}
	
	private void printLemmaNotAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a part of the text.");
	}
	
	private void printLemmaDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Lemma deleted successfully.");
	}
	
	private void printLemmaNotDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a lemma to delete it.");
	}
	
	/* Following methods shouldn't be touched */
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setFocus() {
		
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		
	}
	
	@Override
	public void doSaveAs() {
		
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
