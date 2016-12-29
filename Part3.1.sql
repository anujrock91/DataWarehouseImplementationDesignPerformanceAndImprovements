-----Question 3.1.1--------------ALL PATIENTS CLASSIFIED AS DIFFERENT GROUPS-------------------

SELECT DISTINCT(PATIENTID),
CASE WHEN DIESEASEID = 2 THEN 'A' ELSE 'B' END AS GRP
FROM B_DIAGNOSIS WHERE DIESEASEID NOT IN (-99);


------------All the rest of the parts of Part 3.1 are submitted as a part of the java code--------