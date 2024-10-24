# Jira Mate

Simple Telegram bot that allows to view time logged by user in Jira per day, week or month and send it to Telegram chat.

## How it works?

This bot query 2 Jira API endpoints: one to get worklogs for a period of time, another one to get available loggable time
for this period. After that app composes message which contains information whether a user tracked all available time or
not, and after that send these stats to a Telegram chat.

## Getting Started

### Dependencies

* Java 17
* Maven
* Spring Boot 3.3.2

### Installing

* Pull repository
* Run `mvn clean install`

### Executing program

In order to run this app, first, you have to set up main parameters.

Telegram side:
* integration.telegram.bot-name -- bot name from @BotFather (https://t.me/BotFather)
* integration.telegram.bot-token -- token is from @BotFather, too
* integration.telegram.chat-id -- ID of chat where you plan send messages, here is [how you can lookup it](https://stackoverflow.com/questions/32423837/telegram-bot-how-to-get-a-group-chat-id)

Jira side:
* integration.jira.user-login -- login for Jira
* integration.jira.user-password -- password for Jira
* integration.jira.search-api-url -- search API URL, looks like "/rest/tempo-timesheets/4/worklogs/search" (+ your host at the beginning)
* integration.jira.worktime-api-url -- tempo API URL, looks like "/rest/tempo-core/1/user/schedule" (+ your host at the beginning)
* integration.jira.user-list -- comma-separated user list who you want to monitor; format for one record - "admin/Admin of Jira", where "admin" - user login in Jira, "Admin of Jira" - human-readable variant of name, you can type anything here

After all these parameters set, you can run app in IDE or in CMD by calling `mvn spring-boot:run`.


## Authors

Artem Utkin  
[tema-utkin.ru](tema-utkin.ru)

## To-Do List

- [ ] Disable SSL once per request?
- [x] Javadocs in English

## Version History

* 0.0.2-SNAPSHOT
    * Massive refactoring and different code optimizations
    * spring-boot-starter-parent -> 3.3.2
    * telegrambots -> 6.9.7.1
* 0.0.1-SNAPSHOT
    * Initial Release

## Known issues
* At some time of testing were found users who had different name to use with Jira API. I don't know why this happened and 
I didn't ask Jira administrators for more information. If you too can't get worklogs or anything via API by user, try to
* check its name here: `https://<your-Jira-host>/rest/tempo-core/1/users/search`, `POST` with body: 
```json
{
    "keys": [
        "username-1", "username-2"
    ]
}
```

## License

This project is licensed under the MIT License - see the LICENSE.md file for details