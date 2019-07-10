package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Relation;

public class ResideixADao extends RelationDao {

	public ResideixADao(Connection conn) {
		super(conn);
	}
	
	@Override
	protected int insertSpecificRelation(Relation unit, int commonId) 
			throws SQLException {
		String[] insertColumns = {"relation_id", "artista_id", "lloc_id"};
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
		stmt.setInt(1, commonId);
		stmt.setInt(2, unit.getItsEntity1().getSpecificId());
		stmt.setInt(3, unit.getItsEntity2().getSpecificId());
		
		return executeGetId(stmt);
	}

	@Override
	public String getTable() {
		return "ResideixADao";
	}

	@Override
	public int countEntities() {
		return 2;
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"artista_id", "lloc_id"};
		return entities;
	}
}