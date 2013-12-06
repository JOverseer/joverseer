package org.joverseer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.info.InfoUtils;

public class JOverseerDAO {
	private static final String accessDBURLPrefix = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	private static final String accessDBURLSuffix = ";DriverID=22;READONLY=false}";

	public String[] dropMetadataTables() {
		String[] sql = new String[] { "drop table turns;", "drop table artifacts;", "drop table spells;", "drop table nations;", "drop table hexes;", "drop table games;", };
		return sql;
	}

	public String[] dropDataTables() {
		String[] sql = new String[] { "drop table hexes;", "drop table production;", "drop table armies;", "drop table army_elements;", "drop table characters;", "drop table character_arties;", "drop table character_spells;", "drop table popcenters;", "drop table encounters;", "drop table nation_messages;", "drop table popcenter_production;" };
		return sql;
	}

	public String[] createMetadataTables() {
		String[] sql = new String[] { "create table games(" + "ga_id int," + "ga_no int);",

		"create table turns(" + "tu_id int," + "tu_ga_id int," + "tu_no int);",

		"create table artifacts(" + "ar_ga_id int," + "ar_no int," + "ar_name varchar(100)," + "ar_power1 varchar(100)," + "ar_power2 varchar(100));",

		"create table nations(" + "na_ga_id int," + "na_no int," + "na_allegiance varchar(20)," + "na_name varchar(100)," + "na_short varchar(5));",

		};
		return sql;
	}

	public String[] createDataTables() {
		String[] sql = new String[] { "create table characters(" + "ch_ga_id int," + "ch_tu_no int," + "ch_id int," + "ch_hex int," + "ch_nid varchar(10)," + "ch_name varchar(100)," + "ch_command int," + "ch_command_total int," + "ch_agent int," + "ch_agent_total int," + "ch_emissary int," + "ch_emissary_total int," + "ch_mage int," + "ch_mage_total int," + "ch_stealth int, " + "ch_stealth_total int," + "ch_challenge int," + "ch_health int," + "ch_nation_no int, " + "ch_death_reason varchar(100));",

		"create table hexes(" + "he_ga_id int," + "he_tu_no int," + "he_no int," + "he_terrain varchar(20)," + "he_climate varchar(20));",

		"create table production(" + "pr_ga_id int," + "pr_tu_no int," + "pr_hexno int," + "pr_product varchar(10)," + "pr_amount int," + "pr_terrain varchar(20)," + "pr_climate varchar(20)," + "pr_pc_size int," + "pr_modifier int," + "pr_unmod_amount int);", };
		return sql;
	}

	public void SerializeGame(String filename, Game g) throws RuntimeException {
		Connection conn = null;
		try {
			String databaseURL = accessDBURLPrefix + filename + accessDBURLSuffix;
			java.util.Properties prop = new java.util.Properties();
			prop.put("charSet", "UTF-8");
			prop.put("user", "");
			prop.put("password", "");

			conn = DriverManager.getConnection(databaseURL, prop);
			for (String sql : dropMetadataTables()) {
				try {
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.execute();
				} catch (Exception exc) {
					// do nothing
				}
			}
			for (String sql : dropDataTables()) {
				try {
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.execute();
				} catch (Exception exc) {
					// do nothing
				}
			}
			for (String sql : createMetadataTables()) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.execute();
			}
			for (String sql : createDataTables()) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.execute();
			}
			SerializeGame(conn, g);
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception exc) {
				throw new RuntimeException(exc);
			}
		}
	}

	public void SerializeGame(Connection conn, Game g) {
		try {
			String sql = "insert into games(ga_id, ga_no) values({id}, {no})";
			StatementWrapper sw = new StatementWrapper(sql, conn);
			sw.setInt("{id}", g.getMetadata().getGameNo());
			sw.setInt("{no}", g.getMetadata().getGameNo());
			sw.execute();
			SerializeArtifacts(conn, g);
			SerializeNations(conn, g);

			for (int i = 0; i <= g.getMaxTurn(); i++) {
				Turn t = g.getTurn(i);
				if (t == null)
					continue;
				SerializeTurn(conn, g, t);
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeArtifacts(Connection conn, Game g) {
		try {
			for (ArtifactInfo ai : g.getMetadata().getArtifacts().getItems()) {
				String sql = "insert into artifacts(ar_ga_id, ar_no, ar_name, ar_power1, ar_power2) values({gaid}, {no}, {name}, {power1}, {power2})";
				StatementWrapper sw = new StatementWrapper(sql, conn);
				sw.setInt("{gaid}", g.getMetadata().getGameNo());
				sw.setInt("{no}", ai.getNo());
				sw.setString("{name}", ai.getName());
				sw.setString("{power1}", ai.getPower1());
				sw.setString("{power2}", ai.getPower2());
				sw.execute();
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeNations(Connection conn, Game g) {
		try {
			for (Nation n : g.getMetadata().getNations()) {
				String sql = "insert into nations(na_ga_id, na_no, na_allegiance, na_name, na_short) values({gaid}, {no}, {alleg}, {name}, {short})";
				StatementWrapper sw = new StatementWrapper(sql, conn);
				sw.setInt("{gaid}", g.getMetadata().getGameNo());
				sw.setInt("{no}", n.getNumber().intValue());
				sw.setString("{name}", n.getName());
				sw.setString("{alleg}", n.getAllegiance().toString());
				sw.setString("{short}", n.getShortName());
				sw.execute();
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeTurn(Connection conn, Game g, Turn turn) {
		try {
			String sql = "insert into turns(tu_ga_id, tu_no) values({gaid}, {no})";
			StatementWrapper sw = new StatementWrapper(sql, conn);
			sw.setInt("{gaid}", g.getMetadata().getGameNo());
			sw.setInt("{no}", turn.getTurnNo());
			sw.execute();
			for (Character c : turn.getCharacters().getItems()) {
				SerializeCharacter(conn, g, turn, c);
			}
			for (Hex h : (ArrayList<Hex>) g.getMetadata().getHexes()) {
				SerializeHex(conn, g, turn, h.getHexNo());
			}
			for (PopulationCenter pc : turn.getPopulationCenters().getItems()) {
				SerializeProduction(conn, g, turn, pc);
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeProduction(Connection conn, Game g, Turn turn, PopulationCenter pc) {
		try {
			if (!pc.getInformationSource().equals(InformationSourceEnum.exhaustive))
				return;
			Hex hex = g.getMetadata().getHex(pc.getHexNo());
			HexInfo hi = (HexInfo) turn.getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", pc.getHexNo());
			if (hi == null || hi.getClimate() == null)
				return;
			int pcmod = 100 - Math.max((pc.getSize().getCode().intValue() - 1) * 20, 0);

			for (ProductEnum product : ProductEnum.values()) {
				int climmod = InfoUtils.getClimateModifier(product, hi.getClimate());
				int mod = pcmod * climmod / 100;
				String sql = "insert into production(pr_ga_id, pr_tu_no, pr_hexno, pr_product, pr_amount, pr_terrain, pr_climate, pr_pc_size, pr_modifier, pr_unmod_amount) values({gaid}, {tuno}, {hexno}, {product}, {amount}, {terrain}, {climate}, {pcsize}, {mod}, {unmodamt})";
				StatementWrapper sw = new StatementWrapper(sql, conn);
				sw.setInt("{gaid}", g.getMetadata().getGameNo());
				sw.setInt("{tuno}", turn.getTurnNo());
				sw.setInt("{hexno}", pc.getHexNo());
				sw.setString("{product}", product.getCode());
				Integer pr = pc.getProduction(product);
				if (pr == null)
					pr = 0;
				sw.setInt("{amount}", pr.intValue());
				sw.setString("{terrain}", String.valueOf(hex.getTerrain()));
				sw.setString("{climate}", String.valueOf(hi.getClimate()));
				sw.setInt("{pcsize}", pc.getSize().getCode().intValue());
				sw.setInt("{mod}", mod);
				sw.setInt("{unmodamt}", pr.intValue() * 100 / mod);
				sw.execute();
			}
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeHex(Connection conn, Game g, Turn turn, int hexNo) {
		try {
			Hex hex = g.getMetadata().getHex(hexNo);
			HexInfo hi = (HexInfo) turn.getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", new Integer(hexNo));
			String sql = "insert into hexes(he_ga_id, he_tu_no, he_no, he_terrain, he_climate) values({gaid}, {tuno}, {hexno}, {terrain}, {climate});";
			StatementWrapper sw = new StatementWrapper(sql, conn);
			sw.setInt("{gaid}", g.getMetadata().getGameNo());
			sw.setInt("{tuno}", turn.getTurnNo());
			sw.setInt("{hexno}", hexNo);
			sw.setString("{terrain}", String.valueOf(hex.getTerrain()));
			if (hi != null && hi.getClimate() != null) {
				sw.setString("{climate}", String.valueOf(hi.getClimate()));
			} else {
				sw.setNull("{climate}", Types.VARCHAR);
			}
			sw.execute();
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	public void SerializeCharacter(Connection conn, Game g, Turn turn, org.joverseer.domain.Character c) {
		try {
			String sql = "insert into characters(ch_ga_id, ch_tu_no, ch_id, ch_hex, ch_nation_no, ch_nid, ch_name, ch_command, ch_command_total, ch_agent, ch_agent_total, ch_emissary, ch_emissary_total, ch_mage, ch_mage_total, ch_stealth, ch_stealth_total, ch_challenge, ch_health) " + "values({gaid}, {tuno}, {id}, {hex}, {nation}, {nid}, {name}, {c}, {ct}, {a}, {at}, {e}, {et}, {m}, {mt}, {s}, {st}, {ch}, {h})";
			StatementWrapper sw = new StatementWrapper(sql, conn);
			sw.setInt("{gaid}", g.getMetadata().getGameNo());
			sw.setInt("{tuno}", turn.getTurnNo());
			sw.setInt("{id}", 0);
			sw.setInt("{hex}", c.getHexNo());
			sw.setInt("{nation}", c.getNationNo().intValue());
			sw.setString("{nid}", c.getId());
			sw.setString("{name}", c.getName());
			sw.setInt("{c}", c.getCommand());
			sw.setInt("{ct}", c.getCommandTotal());
			sw.setInt("{a}", c.getAgent());
			sw.setInt("{at}", c.getAgentTotal());
			sw.setInt("{e}", c.getEmmisary());
			sw.setInt("{et}", c.getEmmisaryTotal());
			sw.setInt("{m}", c.getMage());
			sw.setInt("{mt}", c.getMageTotal());
			sw.setInt("{s}", c.getStealth());
			sw.setInt("{st}", c.getStealthTotal());
			sw.setInt("{ch}", c.getChallenge());
			sw.setInt("{h}", c.getHealth() == null ? 0 : c.getHealth().intValue());
			sw.execute();
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

}
