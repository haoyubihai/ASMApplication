echo "########################开始构建---apk---##################"
./gradlew clean
./gradlew build
echo "########################clean 上次构建的缓存"
cd /Users/jiaruihua/Desktop/apk-dex
rm -rf resource
mkdir "resource"
cd /Users/jiaruihua/project/ASMApplication
echo "########################开始移动---apk---到桌面apk-dex/resource"
mv ./app/build/outputs/apk/debug/app-debug.apk /Users/jiaruihua/Desktop/apk-dex/resource
cd /Users/jiaruihua/Desktop/apk-dex/resource
echo "########################进入apk-dex/resource目录"
pwd
echo "########################解压apk"
for i in *.apk;do mv "$i" "${i%.apk}.zip";done
for i in *.zip;do unzip "$i";done
echo "########################清除上次反编译的文件"
cd ../dex2jar/
rm *.dex
rm *.zip
rm ../*.jar
mv ../resource/*.dex ./
echo "########################打包jar"
for i in *.dex;do d2j-dex2jar.sh "$i"; done
echo "########################移动jar"
mv *.jar ../
cd ../
open JD-GUI.app *.jar
echo "########################success"





