echo "########################开始构建---apk---##################"
./gradlew uploadArchives
./gradlew clean
./gradlew installDebug --offline
echo "########################success"





