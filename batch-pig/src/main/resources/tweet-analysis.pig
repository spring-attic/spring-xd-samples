hashtags = LOAD '$inputPath' USING PigStorage('\t') AS (hashtag:chararray, count:int);
sorted = ORDER hashtags BY count DESC;
top10 = LIMIT sorted 10;
STORE top10 INTO '$outputPath';
