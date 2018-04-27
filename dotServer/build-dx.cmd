javac src\main\java\org\jd\dotserver\*.java -d build\classes -encoding utf-8
cd build\classes
jar cf ..\shd.jar .
cd ..
D:\program\android\sdk\build-tools\27.0.3\dx --dex --output=shd.dex shd.jar

