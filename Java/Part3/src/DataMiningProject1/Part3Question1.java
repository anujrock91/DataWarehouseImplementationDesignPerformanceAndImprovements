package DataMiningProject1;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Part3Question1 extends OracleConnection{

	private static Map<Integer, Float> informativeGenesMap = new HashMap<Integer, Float>();
	private static final Float informativeGeneThreshold = 0.01f;

	public static void main(String[] args) {

		try {

			// File handling code
			File file = new File("B:/UB_CS/DataMining/Project1//informativeGenes.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println("T Test and P Values are as follows:");
			pw.println("");

			Statement stmt = conn.createStatement();
			ResultSet uids = stmt.executeQuery("select U_ID from PROBE");
			int count = 0;

			while (uids.next()) {
				count++;
				int uid = uids.getInt(1);
				System.out.print("Gene UID: " + uid);
				PreparedStatement statementForTTest = buildQueryForTTest(uid);
				ResultSet resultSetForTTest = statementForTTest.executeQuery();
				PreparedStatement statementForPValue = buildQueryForPValue(uid);
				ResultSet resultSetForPValue = statementForPValue.executeQuery();

				try {

					while (resultSetForTTest.next()) {
						System.out.print(count + "." + "T-Test Value :  " + resultSetForTTest.getFloat(1));
						pw.print(count + ". " + "T-Test Value :  " + resultSetForTTest.getFloat(1));
					}

					System.out.print(" Count: " + count);
					while (resultSetForPValue.next()) {
						if (resultSetForPValue.getFloat(1) < informativeGeneThreshold) {
							informativeGenesMap.put(uid, resultSetForPValue.getFloat(1));
						}
						System.out.println(" P Value :  " + resultSetForPValue.getFloat(1));
						pw.println(" P Value :  " + resultSetForPValue.getFloat(1));
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				} finally {
					resultSetForTTest.close();
					statementForTTest.close();
					resultSetForPValue.close();
					statementForPValue.close();
				}
			}
			printInformativeGenesMap(pw);
			stmt.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

	private static PreparedStatement buildQueryForPValue(Integer uid) throws SQLException {
		PreparedStatement statementForPValue = conn.prepareStatement(
				"SELECT STATS_T_TEST_INDEP(CASE WHEN bd.DIESEASEID = 2 THEN 'A' ELSE 'B' END, mf.EXP) two_sided_p_value FROM B_DIAGNOSIS bd INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID WHERE DIESEASEID NOT IN (-99) AND p.U_ID =? ORDER BY bd.PATIENTID");

		statementForPValue.setInt(1, uid);
		return statementForPValue;
	}

	private static PreparedStatement buildQueryForTTest(Integer uid) throws SQLException {
		PreparedStatement statementForTTest = conn.prepareStatement(
				"SELECT STATS_T_TEST_INDEP(CASE WHEN bd.DIESEASEID = 2 THEN 'A' ELSE 'B' END, mf.EXP, 'STATISTIC', 'A') T_OBSERVED FROM B_DIAGNOSIS bd INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID WHERE DIESEASEID NOT IN (-99) AND p.U_ID =?  ORDER BY bd.PATIENTID");

		statementForTTest.setInt(1, uid);
		return statementForTTest;
	}

	private static void printInformativeGenesMap(PrintWriter pw) {
		pw.println("");
		pw.println("Informative Genes are as follows:");
		pw.println("");
		Integer count = 0;
		for (Integer uid : informativeGenesMap.keySet()) {
			count++;
			System.out.println("Gene UID: " + uid + " P Value: " + informativeGenesMap.get(uid) + " Count: " + count);
			pw.println(count + ". " + "Gene UID: " + uid + " P Value: " + informativeGenesMap.get(uid));
		}
	}

}
