#  Reading services logs
# Java Runtime exec does not support >, >>, |, & etc. shell operators so I used a bash

journalctl -e -u bot.service >/home/pi/Bot/TelegramBot/src/main/resources/scripts/logs.txt
