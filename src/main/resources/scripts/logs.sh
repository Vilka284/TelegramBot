# This is command for reading services logs
# Java Runtime exec does not support >, >>, |, & etc. shell operators
journalctl -e -u bot.service > /home/pi/Bot/TelegramBot/src/main/resources/scripts/logs.txt