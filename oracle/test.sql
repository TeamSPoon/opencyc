prompt make connection to opencyc
begin cyc.makeconnection(); end;
/
prompt ask for the time
select column_value as cycs_time from the(
  select cyc.askwithvariable(
          '(#$indexicalReferent #$Now ?X)', '?X', 'InferencePSC'
  ) from dual )
/
prompt end connection to opencyc
begin cyc.endconnection(); end;
/
quit
/
