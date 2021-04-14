echo "########################开始构建---apk---##################"
./gradlew uploadArchives
./gradlew clean
./gradlew build

echo "########################success"





