curl -m 5 $1:1024/DEATHPILL | true
scp build/libs/pizzaslackbot-*.jar pi@$1:pizzaslackbot.jar
ssh pi@$1 'bash -s' < run.sh $2 $3