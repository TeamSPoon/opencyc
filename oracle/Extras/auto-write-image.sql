VARIABLE jobno NUMBER;
BEGIN
   DBMS_JOB.SUBMIT(:jobno,
       'cyc.conversevoid( ''(write-image "now.world")'');',
	   SYSDATE, 'SYSDATE+1/24' );
   COMMIT;
END;
/
PRINT jobno
PROMPT CAREFUL!! RUN THIS SCRIPT ONLY ONCE FOR EACH SCHEMA!!!
PROMPT
COLUMN what FORMAT A50
SELECT job,next_date,broken,what FROM user_jobs
/
quit
/
