# InstantRestock

[![Build](https://github.com/spartacus04/InstantRestock/actions/workflows/gradle.yml/badge.svg)](https://github.com/spartacus04/InstantRestock/actions/workflows/gradle.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/spartacus04/instantrestock/badge)](https://www.codefactor.io/repository/github/spartacus04/instantrestock)

![bStats Players](https://img.shields.io/bstats/players/16589)
![bStats Servers](https://img.shields.io/bstats/servers/16589)

![Spigot Downloads](https://img.shields.io/spiget/downloads/88098?label=Spigot%20Downloads)
![Static Badge](https://img.shields.io/badge/Hangar%20Downloads-Not%20available-red?link=https%3A%2F%2Fhangar.papermc.io%2Fspartacus04%2FInfiniteVillagerTrades)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/7pdfxYHV?label=Modrinth%20downloads&color=00cc00)
![GitHub all releases](https://img.shields.io/github/downloads/spartacus04/instantrestock/total?label=Github%20downloads)

>A spigot plugin to allow villagers to trade infinitely

## Overview

The **InstantRestock** plugin is designed to enhance your Minecraft server by providing instant restocking of villager trades. With this plugin, you can easily configure various aspects of trade mechanics to suit your gameplay style.

## Features

- **Max Trades Configuration**: Customize the maximum number of trades a villager can offer using the `/instantrestock config maxTrades <number/infinite>` command.

- **Price Penalty Control**: Enable or disable the price penalty if the Minecraft version is greater than or equal to 1.18 using the `/instantrestock config disablePricePenalty <true/false>` command.

- **Uninstall Mode**: Toggle uninstall mode on or off using the `/instantrestock config uninstallMode <true/false>` command.

- **Travelling Merchants**: Enable or disable infinite trades for travelling merchants with the `/instantrestock config allowTravellingMerchants <true/false>` command.

- **Villager Blacklist**: Add, remove, or list villagers in the blacklist using the `/instantrestock config villagerBlacklist <add/remove/list> <villagertype>` command.

## Usage

### Configuration

Use the following commands to configure the plugin:

- `/instantrestock config maxTrades <number/infinite>`: Set the maximum number of trades a villager can offer.

- `/instantrestock config disablePricePenalty <true/false>`: Enable or disable the price penalty for Minecraft versions >= 1.18.

- `/instantrestock config uninstallMode <true/false>`: Toggle uninstall mode on or off.

- `/instantrestock config allowTravellingMerchants <true/false>`: Enable or disable infinite trades for travelling merchants.

- `/instantrestock config villagerBlacklist <add/remove/list> <villagertype>`: Manage the villager blacklist.

### Reloading the Plugin

- `/instantrestock reload`: Reload the plugin from the config file.

## Permissions

- `instantrestock.reload`: Allows players to use the `/instantrestock reload` command.

- `instantrestock.config`: Allows players to use the configuration commands listed above.

## Support

If you encounter any issues or have questions about the **InstantRestock** plugin, please reach out to us on the [Issue tracker](https://github.com/spartacus04/InstantRestock/issues).

## License

This plugin is distributed under the [MIT License](https://github.com/spartacus04/InstantRestock/blob/master/LICENSE). Feel free to modify and share it as needed.

Enjoy customizing your villager trade experience with the **InstantRestock** plugin on your Minecraft server!
