curl -m 5 localhost:1024/DEATHPILL | true
sleep 5
rm build/deploy/*.jar
cp build/libs/pizzaslackbot-*.jar build/deploy/pizzaslackbot.jar
BUILD_ID=dontKillMe nohup java -jar build/deploy/pizzaslackbot.jar $1 $2 > log.txt 2>&1 &
