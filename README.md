# ATrap - a trap plugin for Minecraft Trap servers
There may be bugs in the plugin, don't ask me for support to fix them. Plugin optimization is good enough, but there are Action Listening events that need to be fixed. I don't plan to fix them if you fix the plugin or we can share an addition here. Have a good experience <3

## Building the Plugin

To build the Atrap plugin, follow these steps:

1. Open the project in IntelliJ IDEA.
2. Make sure to install the necessary Maven dependencies. You can do this by right-clicking on the project in the Project Explorer and selecting "Maven" > "Reimport."
3. Run the following command in the terminal to build the plugin:
   ```
   mvn package
   ```

After the build process is complete, the plugin JAR file will be located in the `target` directory of your project.


# Atrap Plugin - Usage

Welcome to the Atrap plugin for Minecraft! This plugin allows players to manage traps and permissions within the game. Below are the commands available to administrators.

## Commands Overview

### General Commands

- **/atrap reload**
  - Reloads the plugin configuration and language files.
  - Usage: `/atrap reload`

- **/atrap rename <new_name>**
  - Renames the trap at the player's current location.
  - Usage: `/atrap rename <new_name>`

- **/atrap settings**
  - Opens the settings for the trap at the player's current location.
  - Usage: `/atrap settings`

- **/atrap leave**
  - Removes the player from the trap ownership or permissions.
  - Usage: `/atrap leave`

- **/atrap buy <trap_name>**
  - Allows the player to buy the trap at their current location or the specified trap name.
  - Usage: `/atrap buy <trap_name>`

### Admin Commands

These commands require administrator permissions.

- **/atrap admin chunks add <player> <number_of_rights>**
  - Grants additional trap rights to a specified player.
  - Usage: `/atrap admin chunks add <player> <number_of_rights>`

- **/atrap admin new <trap_name>**
  - Creates a new trap at the player's current location with the specified name.
  - Usage: `/atrap admin new <trap_name>`

### Invitation Commands

- **/atrap invite <player>**
  - Sends an invitation to another player to join the trap.
  - Usage: `/atrap invite <player>`

- **/atrap accept <player>**
  - Accepts an invitation to join a trap from the specified player.
  - Usage: `/atrap accept <player>`

- **/atrap deny <player>**
  - Denies an invitation to join a trap from the specified player.
  - Usage: `/atrap deny <player>`

## Example Usages

1. To reload the plugin configuration:
   ```
   /atrap reload
   ```

2. To create a new trap named "MyTrap":
   ```
   /atrap admin new MyTrap
   ```

3. To grant 5 additional trap rights to the player "Steve":
   ```
   /atrap admin chunks add Steve 5
   ```

4. To buy the trap named "MyTrap":
   ```
   /atrap buy MyTrap
   ```

## Permissions

Ensure that the player executing these commands has the necessary permissions defined in the configuration files. The general permission for admin commands can be found under `Admin-Permission`.

## License

This project is licensed under the MIT License. You are free to use, modify, and distribute this software, but you cannot claim it as your own.

## Support and Updates

I don't plan to make any updates or fixes, so please don't ask for help to fix the code.

## Contact
Discord: MrHamzaless
Instagram: hamz.dev
