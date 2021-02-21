# Queue Telegram Bot
## About this project
This is a simple queue bot. Here are some basic logic:
- Each participant can register into a queue at the same day when lab start. /queue command 
- Queue Active one day and then become cleared.
- Participants can leave the queue by /dequeue command.

## Contribution
Feel free to contribute. Here are the list of possible improvements:
- Different subgroups of participants
- Display status of each participant (complete lab, uncompleted lab, left queue etc)
- Participants can write comments on their places in the queue

## Setup and run
When you clone this project download all the dependencies listed in [pom.xml](pom.xml). 
For the database use the PostgreSQL. 
* [init_db.sql](src/main/resources/database/init_db.sql) - script for DB creation.
* [populate_db.sql](src/main/resources/database/populate_db.sql) - script for inserting data into the DB.

Don't forget to apply your personal configuration in the [application.yml](src/main/resources/config/application.yml) file. For example yours bot token or connection to the DB.
