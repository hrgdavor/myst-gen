
dir /s /B src\test\java\*.java > sources.txt
set CP=target/classes
set CP=%CP%;target/dependency/*
"c:\Program Files\Java\jdk1.7.0_51\bin\javac" -cp %CP% -sourcepath src\test\java -d src\gen\java -processor hr.hrg.myst.DbDataProcessor @sources.txt



