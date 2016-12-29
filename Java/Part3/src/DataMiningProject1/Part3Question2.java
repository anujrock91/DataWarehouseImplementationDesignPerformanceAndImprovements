package DataMiningProject1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TestUtils;

public class Part3Question2 extends OracleConnection {

	private static List<TestSample> testSamples = new ArrayList<TestSample>();
	private static Set<Integer> informativeGenesSet = new HashSet<Integer>();
	private static Map<String, List<Double>> allPatientsCorrMap = new HashMap<String, List<Double>>();
	private static Map<String, List<Double>> withoutAllPatientsCorrMap = new HashMap<String, List<Double>>();
	private static Map<String, Double> pValuesMap = new HashMap<String, Double>();

	public static void main(String[] args) throws NumberFormatException, IOException, SQLException {
		// get patient test sample input data and save into an object
		buildTestSamplesFromInputFile();
		informativeGenesSet = buildInformativeGenesSet();
		filterNonInformativeGenesFromTestSamples();
		findAllInformativeGenes();
		findAllGroupAPatients();
		ArrayList<ArrayList<Double>> ALLCor = findCorrelationsForAll();
		
		//Correlation between ALL and NEW Patients
		for(ArrayList<Double> arrALLCorr : ALLCor){
			System.out.println(arrALLCorr);
		}
		
		ArrayList<ArrayList<Double>> NOTALLCor = findCorrelationsForNOTAll();
		//Correlation between NOT ALL and NEW Patients
				for(ArrayList<Double> arrNOTALLCorr : NOTALLCor){
					System.out.println(arrNOTALLCorr);
				}
		
		performTTestBetweenAllAndWithoutAll(ALLCor, NOTALLCor);
	}

	
	
	
	
	private static void buildTestSamplesFromInputFile() throws NumberFormatException, IOException {
		BufferedReader buf = new BufferedReader(
				new FileReader("B://UB_CS/DataMining/Project1/Files/test_samples.txt"));
		String line;
		String[] lineTermsArr;
		Integer row = 0;

		while ((line = buf.readLine()) != null) {
			row++;
			if (row != 1) {
				TestSample testSample = new TestSample();
				List<Integer> patientExpressions = new ArrayList<Integer>();
				Integer column = 0;
				lineTermsArr = line.split("\t");
				for (String term : lineTermsArr) {
					if (!"".equals(term)) {
						if (column == 0) {
							testSample.setGeneUID(Integer.parseInt(term));
						} else {
							patientExpressions.add(Integer.parseInt(term));
						}
					}
					column++;
				}
				testSample.setPatientExpressions(patientExpressions);
				testSamples.add(testSample);
			}
		}
		for (TestSample testSample : testSamples) {
			System.out.println(testSample.getGeneUID() + ":  ");
			for (Integer expression : testSample.getPatientExpressions()) {
				System.out.println(expression);
			}
		}

		buf.close();
	}

	private static void findAllGroupAPatients() {
		try {
			PreparedStatement statementForAllGroupAPatients = buildQueryForAllGroupAPatients();
			ResultSet resultSetForAllGroupAPatients = statementForAllGroupAPatients.executeQuery();

			try {
				Integer count = 0;
				while (resultSetForAllGroupAPatients.next()) {
					count++;
					System.out.print(count + ". " + resultSetForAllGroupAPatients.getInt(1) + ", ");
					System.out.print(resultSetForAllGroupAPatients.getString(2) + ", ");
					System.out.print(resultSetForAllGroupAPatients.getString(3) + ", ");
					System.out.print(resultSetForAllGroupAPatients.getString(4) + ", ");
					System.out.println(resultSetForAllGroupAPatients.getString(5));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				resultSetForAllGroupAPatients.close();
				statementForAllGroupAPatients.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static PreparedStatement buildQueryForAllGroupAPatients() throws SQLException {
		PreparedStatement statementForAllGroupAPatients = conn.prepareStatement(
				"SELECT PATIENT.P_ID,PATIENT.GENDER,PATIENT.NAME,PATIENT.SSN,bd.DIESEASEID FROM B_DIAGNOSIS bd INNER JOIN PATIENT ON bd.PATIENTID = PATIENT.P_ID WHERE DIESEASEID = 2");
		return statementForAllGroupAPatients;
	}

	private static void findAllInformativeGenes() {
		try {
			PreparedStatement statementForInformativeGenes = buildQueryForAllInformativeGenes();
			ResultSet resultSetForInformativeGenes = statementForInformativeGenes.executeQuery();

			try {
				Integer count = 0;
				while (resultSetForInformativeGenes.next()) {
					count++;
					System.out.print(count + ". " + resultSetForInformativeGenes.getInt(1) + ", ");
					System.out.print(resultSetForInformativeGenes.getString(2) + ", ");
					System.out.print(resultSetForInformativeGenes.getString(3) + ", ");
					System.out.print(resultSetForInformativeGenes.getString(4) + ", ");
					System.out.print(resultSetForInformativeGenes.getString(5) + ", ");
					System.out.print(resultSetForInformativeGenes.getInt(6) + ", ");
					System.out.println(resultSetForInformativeGenes.getString(7));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				resultSetForInformativeGenes.close();
				statementForInformativeGenes.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static PreparedStatement buildQueryForAllInformativeGenes() throws SQLException {
		PreparedStatement statementForInformativeGenes = conn.prepareStatement(
				"SELECT * FROM GENE WHERE U_ID IN (SELECT DISTINCT(p.U_ID) FROM B_DIAGNOSIS bd INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID WHERE bd.DIESEASEID IN (2) AND p.U_ID IN (4826120,83398521,40567338, 37998407, 43866587, 13947282,31308500,58792011,74496827,85557586,60661836,41333415,48199244,88257558,15295292,21633757,58672549,69156037,53478188,97606543,41464216,88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884))");
		return statementForInformativeGenes;
	}

	private static void filterNonInformativeGenesFromTestSamples() {
		System.out.println("Before Removal:" + testSamples.size());
		List<TestSample> testSamplesToRemove = new ArrayList<TestSample>();
		for (TestSample testSample : testSamples) {
			if (!informativeGenesSet.contains(testSample.getGeneUID())) {
				testSamplesToRemove.add(testSample);
			}
		}
		testSamples.removeAll(testSamplesToRemove);
		System.out.println("After Removal:" + testSamples.size());
	}

	private static Set<Integer> buildInformativeGenesSet() {
		Set<Integer> informativeGenes = new HashSet<Integer>();
		try {
			PreparedStatement statementForInformativeGenes = buildQueryForAllInformativeGenes();
			ResultSet resultSetForInformativeGenes = statementForInformativeGenes.executeQuery();

			try {
				while (resultSetForInformativeGenes.next()) {
					informativeGenes.add(resultSetForInformativeGenes.getInt(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				resultSetForInformativeGenes.close();
				statementForInformativeGenes.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("There are total " + informativeGenes.size() + " informative genes");
		return informativeGenes;
	}

	
	private static void performTTestBetweenAllAndWithoutAll(ArrayList<ArrayList<Double>> ALL, ArrayList<ArrayList<Double>> NOTALL){
		System.out.println("T-Test");
		int numberOfPatients = ALL.size();
		for(int i=0;i<numberOfPatients;i++){
			double[] sample1 = ArrayUtils.toPrimitive((Double [])ALL.get(i).toArray(new Double[ALL.get(i).size()]));
			double[] sample2 = ArrayUtils.toPrimitive((Double [])NOTALL.get(i).toArray(new Double[NOTALL.get(i).size()]));
			double pValue = (TestUtils.homoscedasticTTest(sample1, sample2));
			if(pValue< 0.01){
				System.out.println("Patient "+ (i+1) + " : is : ALL : "+ " P-value:  " + pValue);
			}
			else{
				System.out.println("Patient "+ (i+1) + " : is : NOT ALL : "+ " P-value:  " + pValue);
			}
		}
	}
	
	
	
	public static ArrayList<ArrayList<Double>> findCorrelationsForAll() throws SQLException{
		ArrayList<ArrayList<Double>> calculatedCorALL = new ArrayList<ArrayList<Double>>();
		
		//Finding the Id's of the patient in group A
		ArrayList<Integer> ALLPatients = getPatientsALLList();
		
		//finding the number of patients
		int numberOfNewPatients = testSamples.get(0).getPatientExpressions().size();
	
		for(int i=0; i<numberOfNewPatients; i++){
			//New Patient Expression ArrayList
			System.out.println("Processing For the new patient : " + i);
			ArrayList<Integer> newPatientExp = new ArrayList<Integer>();
			
			//Constructing the Expression ArrayList and double Array for the new patient 'i'
			Iterator<TestSample> testSampleObject = testSamples.iterator(); 
			while(testSampleObject.hasNext()){
				int newPatientExpValue = testSampleObject.next().getPatientExpressions().get(i);
				newPatientExp.add(newPatientExpValue);
			}
			double [] newPatientExpArray = new double[newPatientExp.size()];
			for(int doubleArray=0; doubleArray< newPatientExp.size(); doubleArray++){
				newPatientExpArray[doubleArray] = newPatientExp.get(doubleArray);
			}
			
			//Iterating over all the patients in ALL group
			Iterator<Integer> ALLPatientIterator = ALLPatients.iterator();
			
			//Creating an Array of Correlation between newPatient and ALL Patients
			ArrayList<Double> ALLPatientVsNewPatient = new ArrayList<Double>();	
			
			while(ALLPatientIterator.hasNext()){
				//Constructing the double array of Expression values for Patient A of ALL group
				int ALLPatientID = ALLPatientIterator.next().intValue();

				ArrayList<Integer> expValueForOnePatientALL = getExpressionListForPatientIDALL(ALLPatientID);
				double [] expValueForOnePatientALLArray = new double[expValueForOnePatientALL.size()];
				for(int doubleArray = 0; doubleArray < expValueForOnePatientALL.size() ; doubleArray ++){
					expValueForOnePatientALLArray [doubleArray] = expValueForOnePatientALL.get(doubleArray);
				}
				try{
					ALLPatientVsNewPatient.add(new PearsonsCorrelation().correlation(expValueForOnePatientALLArray, newPatientExpArray));
				}
				catch(Exception e){
					continue;
				}
			}
			calculatedCorALL.add(i, ALLPatientVsNewPatient);
		}
		return calculatedCorALL;
	}
	
	
	
	public static ArrayList<ArrayList<Double>> findCorrelationsForNOTAll() throws SQLException{
		ArrayList<ArrayList<Double>> calculatedCorNOTALL = new ArrayList<ArrayList<Double>>();
		
		//Finding the Id's of the patient in group B
		ArrayList<Integer> NOTALLPatients = getPatientsNOTALLList();
		
		//finding the number of patients
		int numberOfNewPatients = testSamples.get(0).getPatientExpressions().size();
		
		for(int i=0; i<numberOfNewPatients; i++){
			//New Patient Expression ArrayList
			System.out.println("Processing For the new patient : " + i);
			ArrayList<Integer> newPatientExp = new ArrayList<Integer>();
			
			//Constructing the Expression ArrayList and double Array for the new patient 'i'
			Iterator<TestSample> testSampleObject = testSamples.iterator(); 
			while(testSampleObject.hasNext()){
				int newPatientExpValue = testSampleObject.next().getPatientExpressions().get(i);
				newPatientExp.add(newPatientExpValue);
			}
			double [] newPatientExpArray = new double[newPatientExp.size()];
			for(int doubleArray=0; doubleArray< newPatientExp.size(); doubleArray++){
				newPatientExpArray[doubleArray] = newPatientExp.get(doubleArray);
			}
			
			//Iterating over all the patients in NOT ALL group
			Iterator<Integer> NOTALLPatientIterator = NOTALLPatients.iterator();
			
			//Creating an Array of Correlation between newPatient and NOT ALL Patients
			ArrayList<Double> NOTALLPatientVsNewPatient = new ArrayList<Double>();
			
			while(NOTALLPatientIterator.hasNext()){
				//Constructing the double array of Expression values for Patient B of NOT ALL group
				int NOTALLPatientID = NOTALLPatientIterator.next().intValue();
			
				ArrayList<Integer> expValueForOnePatientNOTALL = getExpressionListForPatientIDNOTALL(NOTALLPatientID);
				double [] expValueForOnePatientNOTALLArray = new double[expValueForOnePatientNOTALL.size()];
				for(int doubleArray = 0; doubleArray < expValueForOnePatientNOTALL.size() ; doubleArray ++){
					expValueForOnePatientNOTALLArray [doubleArray] = expValueForOnePatientNOTALL.get(doubleArray);
				}
				try{
					NOTALLPatientVsNewPatient.add(new PearsonsCorrelation().correlation(expValueForOnePatientNOTALLArray, newPatientExpArray));
				}
				catch(Exception e){
					continue;
				}
			}
			calculatedCorNOTALL.add(i, NOTALLPatientVsNewPatient);	
		}
		return calculatedCorNOTALL;
	}
	
	
	
	
	private static ArrayList<Integer> getPatientsALLList() throws SQLException{
		ArrayList<Integer> patientIds = new ArrayList<Integer>();
		Statement st = conn.createStatement();
		ResultSet patientDetails = st.executeQuery("SELECT PATIENT.P_ID,PATIENT.GENDER,PATIENT.NAME,PATIENT.SSN,bd.DIESEASEID FROM B_DIAGNOSIS bd INNER JOIN PATIENT ON bd.PATIENTID = PATIENT.P_ID WHERE DIESEASEID = 2");
		while(patientDetails.next()){
			patientIds.add(patientDetails.getInt(1));
		}
		patientDetails.close();
		st.close();
		System.out.println("patient Ids added in method getPatientsALLList()");
		return patientIds;
	}
	
	
	private static ArrayList<Integer> getPatientsNOTALLList() throws SQLException{
		ArrayList<Integer> patientIds = new ArrayList<Integer>();
		Statement st = conn.createStatement();
		ResultSet patientDetails = st.executeQuery("SELECT PATIENT.P_ID,PATIENT.GENDER,PATIENT.NAME,PATIENT.SSN,bd.DIESEASEID FROM B_DIAGNOSIS bd INNER JOIN PATIENT ON bd.PATIENTID = PATIENT.P_ID WHERE DIESEASEID  NOT IN (2,-99)");
		while(patientDetails.next()){
			patientIds.add(patientDetails.getInt(1));
		}
		patientDetails.close();
		st.close();
		System.out.println("patient Ids added in method getPatientsNOTALLList()");
		return patientIds;
	}
	
	
	private static ArrayList<Integer> getExpressionListForPatientIDALL(int id) throws SQLException{
		ArrayList<Integer> patientExp = new ArrayList<Integer>();
		PreparedStatement pst = conn.prepareStatement("SELECT bd.PATIENTID, bd.DIESEASEID, bcs.SAMPLEID,mf.PB_ID,p.U_ID,mf.EXP FROM B_DIAGNOSIS bd INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID WHERE bd.DIESEASEID IN (2) AND bd.PATIENTID = ? AND p.U_ID IN (4826120, 83398521,40567338, 37998407, 43866587, 13947282,31308500,58792011,74496827,85557586,60661836,41333415,48199244,88257558,15295292,21633757,58672549,69156037,53478188,97606543,41464216,88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884)");
		pst.setInt(1, id);
		ResultSet patientDetailsWithExp = pst.executeQuery();
		while(patientDetailsWithExp.next()){
			patientExp.add(patientDetailsWithExp.getInt(6));
		}
		patientDetailsWithExp.close();
		pst.close();
		return patientExp;
	}
	
	
	
	private static ArrayList<Integer> getExpressionListForPatientIDNOTALL(int id) throws SQLException{
		ArrayList<Integer> patientExp = new ArrayList<Integer>();
		PreparedStatement pst = conn.prepareStatement("SELECT bd.PATIENTID, bd.DIESEASEID, bcs.SAMPLEID,mf.PB_ID,p.U_ID,mf.EXP FROM B_DIAGNOSIS bd INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID WHERE bd.DIESEASEID NOT IN (2,-99) AND bd.PATIENTID = ? AND p.U_ID IN (4826120, 83398521,40567338, 37998407, 43866587, 13947282,31308500,58792011,74496827,85557586,60661836,41333415,48199244,88257558,15295292,21633757,58672549,69156037,53478188,97606543,41464216,88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884)");
		pst.setInt(1, id);
		ResultSet patientDetailsWithExp = pst.executeQuery();
		while(patientDetailsWithExp.next()){
			patientExp.add(patientDetailsWithExp.getInt(6));
		}
		patientDetailsWithExp.close();
		pst.close();
		return patientExp;
	}
	
	
	

}
