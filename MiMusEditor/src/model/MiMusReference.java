package model;

import java.util.List;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A MiMusReference is an instance of a Bibliography in a
 * certain MiMusEntry document of the corpora. It specifies what
 * bibliography entry the reference is addressed at, as well as
 * the specific pages where the reference happens for such document.
 * 
 * The conceptual relations between these classes can be summarized as:
 * 
 * 1 <MiMusEntry> has several <MiMusReference>
 * 1 <MiMusReference> has 1 <Bibliography>
 * Hence, 1 <Bibliography> is indirectly linked to several <MiMusEntry>
 * 
 * This allows for definition of information specific to the <MiMusEntry>
 * for each appearance of the same <Bibliography>.
 * 
 * Its String representation is constructed from that from its Bibliography,
 * just by attaching the pages field at the end of it.
 * 
 */
public class MiMusReference extends ConcreteUnit {
	
	private Bibliography itsBiblio;
	private Document itsDocument;
	private String page;
	private int type;
	private int id;
	
	public MiMusReference() {}
	
	public MiMusReference(List<Unit> allBiblio) {
		super(allBiblio);
	}
	
	public MiMusReference(Bibliography itsBiblio, Document itsDocument,
			String page, int type, int id) {
		this.itsBiblio = itsBiblio;
		this.itsDocument = itsDocument;
		this.page = page;
		this.setType(type);
		this.setId(id);
	}
	
	public String toString() {
		return itsBiblio.toString() + ", p. " + page + ".";
	}
	
	/* Getters and setters */
	
	public Bibliography getItsBiblio() {
		return itsBiblio;
	}
	public void setItsBiblio(Bibliography itsBiblio) {
		this.itsBiblio = itsBiblio;
	}
	public Document getItsDocument() {
		return itsDocument;
	}
	public void setItsDocument(Document itsDocument) {
		this.itsDocument = itsDocument;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public static boolean containsBibliography(List<MiMusReference> references, 
			Bibliography biblio) {
		for (MiMusReference ref: references) {
			if (ref.getItsBiblio().equals(biblio)) {
				return true;
			}
		}
		return false;
	}
}
