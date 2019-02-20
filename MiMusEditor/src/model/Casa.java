package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;

public class Casa extends Entity {
	
	private String nomComplet;
	private String titol;
	private String cort;
	
	public Casa() {}
	
	public Casa(int id, String nomComplet, String titol, String cort) {
		super(id);
		this.nomComplet = nomComplet;
		this.titol = titol;
		this.cort = cort;
	}

	@Override
	public Casa fromXMLElement(Element elem) {
		String nomComplet = elem.getElementsByTagName("nom_complet")
				.item(0).getTextContent();
		String titol = elem.getElementsByTagName("titol")
				.item(0).getTextContent();
		String cort = elem.getElementsByTagName("cort")
				.item(0).getTextContent();
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		return new Casa(id, nomComplet, titol, cort);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagNomComplet = doc.createElement("nom_complet");
		tagNomComplet.appendChild(doc.createTextNode(getNomComplet()));
		Element tagTitol = doc.createElement("titol");
		tagTitol.appendChild(doc.createTextNode(getTitol()));
		Element tagCort = doc.createElement("cort");
		tagCort.appendChild(doc.createTextNode(getCort()));
		Element tagID = doc.createElement("id");
		tagID.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagNomComplet);
		tagEntry.appendChild(tagTitol);
		tagEntry.appendChild(tagCort);
		tagEntry.appendChild(tagID);
		return tagEntry;
	}

	@Override
	public String getWritableName() {
		return "casa";
	}

	@Override
	public String getWritableCategory() {
		return "cases";
	}

	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}

	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getCort() {
		return cort;
	}
	public void setCort(String cort) {
		this.cort = cort;
	}
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openCasa().getDoc();
		NodeList nl = doc.getElementsByTagName("casa");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Casa().fromXMLElement(elem));
			}
		}
		return entries;
	}
}