package model;

/**
 * MiMusLibraryIdentifier is a container for a part of the information
 * associated with a MiMus Document.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusLibraryIdentifier {
	
	private String archive;
	private String series;
	private String subseries1;
	private String subseries2;
	private String number;
	private String page;

	public MiMusLibraryIdentifier() {}

	/**
	 * String representation of a library identification,
	 * where all components are separated by commas except
	 * for the separation between subseries and number, which
	 * uses a whitespace regarding the specification.
	 */
	@Override
	public String toString() {
		String str = "";
		if (archive != null)
			str += archive + ", ";
		if (series != null)
			str += series + ", ";
		if (subseries1 != null)
			str += subseries1 + ", ";
		if (subseries2 != null)
			str += subseries2 + " ";
		if (number != null)
			str += number + ", ";
		if (page != null)
			str += page + ", ";
		/* 
		 * Remove last ", " by removing 2 last characters (length-2).
		 * If everything is null, return empty sequence.
		 */
		return str.substring(0, Math.max(0,str.length()-2));
	}

	public String getArchive() {
		return archive;
	}
	public void setArchive(String archive) {
		this.archive = archive;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getSubseries1() {
		return subseries1;
	}
	public void setSubseries1(String subseries1) {
		this.subseries1 = subseries1;
	}
	public String getSubseries2() {
		return subseries2;
	}
	public void setSubseries2(String subseries2) {
		this.subseries2 = subseries2;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
}
