curl localhost:1024/DEATHPILL
sleep 5
rm build/deploy/*.jar
cp build/libs/pizzaslackbot-*.jar build/deploy/pizzaslackbot.jar
BUILD_ID=dontKillMe nohup bash java -jar build/deploy/pizzaslackbot.jar $1 $2 > log.txt 2>&1
