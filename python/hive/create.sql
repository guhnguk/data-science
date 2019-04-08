CREATE  TABLE `vcrm_6442251.mig_tb`(
  `user_id` string, 
  `trip_id` string, 
  `seq_num` bigint, 
  `x` double, 
  `y` double, 
  `distance` double, 
  `velocity` double, 
  `accelation` double, 
  `accel_id` int)
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  'hdfs://nameservice4/user/hive/warehouse/vcrm_6442251.db/mig_tb'
TBLPROPERTIES (
  'COLUMN_STATS_ACCURATE'='true', 
  'numFiles'='88', 
  'numRows'='0', 
  'rawDataSize'='0', 
  'totalSize'='23219644524', 
  'transient_lastDdlTime'='1425350540')
;
