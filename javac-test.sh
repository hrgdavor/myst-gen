
find -name "src/test/java/*.java" > sources.txt
javac -cp target\classes -sourcepath src\test\java -d target\test-classes -verbose @sources.txt
