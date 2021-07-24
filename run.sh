# compile and run tests for pointer analysis

# the output directory
rm -r sootOutput
mkdir sootOutput
# compile the test cases
#javac -cp soot.jar -d sootOutput sootInput/*.java
find sootInput -name "*.java" -exec javac -cp sootInput/ -d sootOutput/ {} \;
# copy the soot jar package
cp sootInput/jce.jar sootOutput/jce.jar
cp sootInput/rt.jar sootOutput/rt.jar

mvn clean package
cp target/MyPointerAnalysis-1.0-SNAPSHOT.jar analyzer.jar

# default classes for test cases
classes=( # Basic Tests
          "Hello")

# we can also pass arguments as classes
if [ "$#" -ge 1 ]
then
	classes=("$@")
fi

# run the analysis for each class and place the result
for class in "${classes[@]}"; do
	rm result.txt
	java -jar analyzer.jar sootOutput test."$class"
	if [ -f result.txt ]
	then
		cp result.txt result-"$class".txt
	fi
done



