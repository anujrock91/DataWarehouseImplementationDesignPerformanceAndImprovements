package DataMiningProject1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Part2Question6 extends OracleConnection {

	private static Map<Integer, List<Double>> allPatientsMap = new LinkedHashMap<Integer, List<Double>>();
	private static Map<Integer, List<Double>> amlPatientsMap = new LinkedHashMap<Integer, List<Double>>();
	private static final Integer goId = 7154;

	public static void main(String[] args) {
		findAllPatientsWithGoId();
		findAmlPatientsWithGoId();
		printAllAndAmlPatientMaps();
		findAndPrintCorrelationsAmongstAll();
		findAndPrintCorrelationsAmongstAllAndAml();
	}

	private static void findAllPatientsWithGoId() {
		try {
			PreparedStatement statementForAllPatientsWithGoId = buildQueryForAllPatientsWithGoId();
			ResultSet resultSetForAllPatientsWithGoId = statementForAllPatientsWithGoId.executeQuery();

			try {
				List<Double> tempPatientExpressionsList = new ArrayList<Double>();
				Integer tempPatientId = null;
				Double tempPatientExpression = null;
				while (resultSetForAllPatientsWithGoId.next()) {
					tempPatientId = resultSetForAllPatientsWithGoId.getInt(1);
					tempPatientExpression = resultSetForAllPatientsWithGoId.getDouble(2);
					if (!allPatientsMap.containsKey(tempPatientId)) {
						if (tempPatientExpressionsList.size() > 0) {
							tempPatientExpressionsList.clear();
						}
					}
					tempPatientExpressionsList.add(tempPatientExpression);
					allPatientsMap.put(tempPatientId, tempPatientExpressionsList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				resultSetForAllPatientsWithGoId.close();
				statementForAllPatientsWithGoId.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void findAmlPatientsWithGoId() {
		try {
			PreparedStatement statementForAmlPatientsWithGoId = buildQueryForAmlPatientsWithGoId();
			ResultSet resultSetForAmlPatientsWithGoId = statementForAmlPatientsWithGoId.executeQuery();

			try {
				List<Double> tempPatientExpressionsList = new ArrayList<Double>();
				Integer tempPatientId = null;
				Double tempPatientExpression = null;
				while (resultSetForAmlPatientsWithGoId.next()) {
					tempPatientId = resultSetForAmlPatientsWithGoId.getInt(1);
					tempPatientExpression = resultSetForAmlPatientsWithGoId.getDouble(2);
					if (!amlPatientsMap.containsKey(tempPatientId)) {
						if (tempPatientExpressionsList.size() > 0) {
							tempPatientExpressionsList.clear();
						}
					}
					tempPatientExpressionsList.add(tempPatientExpression);
					amlPatientsMap.put(tempPatientId, tempPatientExpressionsList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				resultSetForAmlPatientsWithGoId.close();
				statementForAmlPatientsWithGoId.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static PreparedStatement buildQueryForAllPatientsWithGoId() throws SQLException {
		PreparedStatement statementForAllPatientsWithGoId = conn.prepareStatement(
				"SELECT BD.PATIENTID, MF.EXP, GF.GO_ID, BD.DIESEASEID FROM B_DIAGNOSIS BD INNER JOIN B_CLINICAL_SAMPLE BCS ON BD.PATIENTID = BCS.PATIENTID INNER JOIN MICROARRAY_FACT MF ON BCS.SAMPLEID = MF.S_ID INNER JOIN PROBE P ON MF.PB_ID = P.PB_ID INNER JOIN GENE_FACT GF ON P.U_ID = GF.U_ID WHERE BD.DIESEASEID IN (2) AND GF.GO_ID =?");

		statementForAllPatientsWithGoId.setInt(1, goId);
		return statementForAllPatientsWithGoId;
	}

	private static PreparedStatement buildQueryForAmlPatientsWithGoId() throws SQLException {
		PreparedStatement statementForAmlPatientsWithGoId = conn.prepareStatement(
				"SELECT BD.PATIENTID, MF.EXP, GF.GO_ID, BD.DIESEASEID FROM B_DIAGNOSIS BD INNER JOIN B_CLINICAL_SAMPLE BCS ON BD.PATIENTID = BCS.PATIENTID INNER JOIN MICROARRAY_FACT MF ON BCS.SAMPLEID = MF.S_ID INNER JOIN PROBE P ON MF.PB_ID = P.PB_ID INNER JOIN GENE_FACT GF ON P.U_ID = GF.U_ID WHERE BD.DIESEASEID IN (3) AND GF.GO_ID =?");

		statementForAmlPatientsWithGoId.setInt(1, goId);
		return statementForAmlPatientsWithGoId;
	}

	private static void printAllAndAmlPatientMaps() {
		Integer index1 = 1;
		for (Integer key : allPatientsMap.keySet()) {
			System.out.println(index1 + ". Key= " + key + ", Value=" + allPatientsMap.get(key));
			index1++;
		}

		Integer index2 = 1;
		for (Integer key : amlPatientsMap.keySet()) {
			System.out.println(index2 + ". Key= " + key + ", Value=" + amlPatientsMap.get(key));
			index2++;
		}
		
		System.out.println();
		System.out.println();
	}

	private static void findAndPrintCorrelationsAmongstAll() {
		Double correlationsSum = 0.0;
		Integer size1;
		Integer size2;
		List<List<Double>> allPatientExpressions = new ArrayList<List<Double>>(allPatientsMap.values());

		for (int i = 0; i < allPatientExpressions.size(); i++) {
			size1 = allPatientExpressions.get(i).size();
			double[] sample1 = ArrayUtils
					.toPrimitive((Double[]) allPatientExpressions.get(i).toArray(new Double[size1]));

			for (int j = i + 1; j < allPatientExpressions.size(); j++) {
				size2 = allPatientExpressions.get(j).size();
				double[] sample2 = ArrayUtils
						.toPrimitive((Double[]) allPatientExpressions.get(j).toArray(new Double[size2]));

				if (sample1.length == sample2.length) {
					Double correlation = new PearsonsCorrelation().correlation(sample1, sample2);
					correlationsSum = correlationsSum + correlation;
				} else {
					break;
				}
			}
		}

		Integer totalAllPatients = allPatientExpressions.size();
		Double averageCorrelation = (correlationsSum / (((totalAllPatients) * (totalAllPatients - 1)) / 2));
		System.out.println("Correlation Sum Amongst All Patients: " + correlationsSum);
		System.out.println("Average Correlation Amongst ALL Patients: " + averageCorrelation);
		System.out.println();
	}

	private static void findAndPrintCorrelationsAmongstAllAndAml() {
		Double correlationsSum = 0.0;
		Integer size1;
		Integer size2;
		List<List<Double>> allPatientExpressions = new ArrayList<List<Double>>(allPatientsMap.values());
		List<List<Double>> amlPatientExpressions = new ArrayList<List<Double>>(amlPatientsMap.values());

		for (int i = 0; i < allPatientExpressions.size(); i++) {
			size1 = allPatientExpressions.get(i).size();
			double[] sample1 = ArrayUtils
					.toPrimitive((Double[]) allPatientExpressions.get(i).toArray(new Double[size1]));

			for (int j = 0; j < amlPatientExpressions.size(); j++) {
				size2 = amlPatientExpressions.get(j).size();
				double[] sample2 = ArrayUtils
						.toPrimitive((Double[]) amlPatientExpressions.get(j).toArray(new Double[size2]));

				if (sample1.length == sample2.length) {
					Double correlation = new PearsonsCorrelation().correlation(sample1, sample2);
					correlationsSum = correlationsSum + correlation;
				} else {
					break;
				}
			}
		}

		Integer totalAllPatients = allPatientExpressions.size();
		Integer totalAmlPatients = amlPatientExpressions.size();
		Double averageCorrelation = (correlationsSum / ((totalAllPatients) * (totalAmlPatients)));
		System.out.println("Correlation Sum Amongst ALL and AML Patients: " + correlationsSum);
		System.out.println("Average Correlation Amongst ALL and AML Patients: " + averageCorrelation);
	}

}
