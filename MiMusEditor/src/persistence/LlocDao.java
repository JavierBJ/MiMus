package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Lloc;

public class LlocDao extends EntityDao<Lloc> {

	public LlocDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Lloc unit, int entId) 
			throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"ent_id", "nom_complet", "regne", "area"};
		String sql = "INSERT INTO " + getTable() + " (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += insertColumns[i] + ", ";
		}
		sql += insertColumns[insertColumns.length-1] + ") VALUES (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += "?, ";
		}
		sql += "?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, entId);
		stmt.setString(2, unit.getNomComplet());
		stmt.setInt(3, unit.getRegne());
		stmt.setInt(4, unit.getArea());
		
		return executeGetId(stmt);
	}

	@Override
	protected Lloc make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		int regne = rs.getInt("regne");
		int area = rs.getInt("area");
		return new Lloc(id, nomComplet, regne, area);
	}

	@Override
	public String getTable() {
		return "Lloc";
	}

}
