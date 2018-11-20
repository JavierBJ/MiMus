package util.xml;

import org.w3c.dom.Element;

/**
 * 
 * In MiMus, an object is Writable if it can be written down to
 * an XML document that conforms the annotated corpus. Classes
 * implementing MiMusWritable are, in general, model objects that
 * have specific parameters which must be expressed as an XML
 * element in the corpus. The method toXMLElement() returns this
 * XML element.
 * 
 * In order to write an object into an XML, another information
 * needed is where to store it, i.e. under which XML entry. The
 * method getWritableCategory() returns the name of the XML entry
 * that will be parent of the element created by toXMLElement().
 * 
 * Both methods are accessed by MiMusXMLWriter, which contains the
 * functionality to write the corpus to XML files, and it's called
 * from the UI actions.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public interface MiMusWritable {
	
	/**
	 * Converts a model object into an XML Element that can be
	 * appended to an XML Document.
	 */
	public Element toXMLElement();
	
	/**
	 * Returns the name of the XML entry that must be used as
	 * parent of the XML Element generated by toXMLElement().
	 */
	public String getWritableCategory();
	
}
