package model;

public class GenereLiterari extends Entity {

	private String nomComplet;
	private String nomFrances;
	private String nomOccita;
	private String definicio;
	
	public GenereLiterari(int id, int specId, String nomComplet, String nomFrances,
			String nomOccita, String definicio) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.nomFrances = nomFrances;
		this.nomOccita = nomOccita;
		this.definicio = definicio;
	}
	
	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	public String toString() {
		return getLemma();
	}

	public String getNomComplet() {
		return nomComplet;
	}

	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}

	public String getNomFrances() {
		return nomFrances;
	}

	public void setNomFrances(String nomFrances) {
		this.nomFrances = nomFrances;
	}

	public String getNomOccita() {
		return nomOccita;
	}

	public void setNomOccita(String nomOccita) {
		this.nomOccita = nomOccita;
	}

	public String getDefinicio() {
		return definicio;
	}

	public void setDefinicio(String definicio) {
		this.definicio = definicio;
	}
}
