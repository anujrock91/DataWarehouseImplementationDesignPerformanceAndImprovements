--------QUESTION 3.2.1------------FINDING INFORMATIVE GENES WRT ALL(GROUP A)-------------------------------
--------------------How informative genes have been founded is done as a part of the problem in Part3 1,2,3--------
SELECT * FROM GENE WHERE U_ID IN (
SELECT DISTINCT(p.U_ID)
FROM B_DIAGNOSIS bd
INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID
INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID
INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID 
WHERE bd.DIESEASEID IN (2) AND p.U_ID IN (4826120,
83398521,40567338, 37998407, 43866587, 13947282,31308500,
58792011,74496827,85557586,60661836,41333415,48199244,88257558,
15295292,21633757,58672549,69156037,53478188,97606543,41464216,
88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,
47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884)
);


-----QUESTION 3.2.2---------Find all Patient with ALL group A-------------------------------------

SELECT PATIENT.P_ID,PATIENT.GENDER,PATIENT.NAME,PATIENT.SSN,bd.DIESEASEID
FROM B_DIAGNOSIS bd INNER JOIN PATIENT ON bd.PATIENTID = PATIENT.P_ID WHERE DIESEASEID = 2;

------------------------------------------------------------------------------------------------



-----QUESTION 3.3.3--------The query here gives expression corresponding to a patient ID for infromative genes in ALL----------
---------------------------This query is used as a part of JAVA code to calculate the corelation values for new patients-------
---------------------------Also this part is solved in JAVA, hence, by running the code directly one may see that what is the corelation of
---------------------------a New patient with the the patients of ALL group(GROUP A) for informative genes------------------------------

SELECT bd.PATIENTID, bd.DIESEASEID, bcs.SAMPLEID,mf.PB_ID,p.U_ID,mf.EXP
FROM B_DIAGNOSIS bd
INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID
INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID
INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID 
WHERE bd.DIESEASEID IN (2) AND bd.PATIENTID = 13258 AND p.U_ID IN (4826120,
83398521,40567338, 37998407, 43866587, 13947282,31308500,
58792011,74496827,85557586,60661836,41333415,48199244,88257558,
15295292,21633757,58672549,69156037,53478188,97606543,41464216,
88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,
47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884);


-----QUESTION 3.3.5--------The query here gives expression corresponding to a patient ID for infromative genes in NOT ALL----------
---------------------------This query is used as a part of JAVA code to calculate the corelation values for new patients-------
---------------------------Also this part is solved in JAVA, hence, by running the code directly one may see that what is the corelation of
---------------------------a New patient with the the patients of NOT ALL group(GROUP B) for informative genes------------------------------

SELECT bd.PATIENTID, bd.DIESEASEID, bcs.SAMPLEID,mf.PB_ID,p.U_ID,mf.EXP
FROM B_DIAGNOSIS bd
INNER JOIN B_CLINICAL_SAMPLE bcs ON bd.PATIENTID = bcs.PATIENTID
INNER JOIN MICROARRAY_FACT mf ON bcs.SAMPLEID = mf.S_ID
INNER JOIN PROBE p ON mf.PB_ID = p.PB_ID 
WHERE bd.DIESEASEID NOT IN (2,-99) AND bd.PATIENTID = 13258 AND p.U_ID IN (4826120,
83398521,40567338, 37998407, 43866587, 13947282,31308500,
58792011,74496827,85557586,60661836,41333415,48199244,88257558,
15295292,21633757,58672549,69156037,53478188,97606543,41464216,
88596261,94113401,18493181,45926811,11333636,1433276,31997186,28863379,
47276861,52948490,75434512,92443312,24984526,75492172,16073088,87592194,65772884);


------Question 6 is totally solved in JAVA-----------------------------------------------------------------------------------------------------------------
