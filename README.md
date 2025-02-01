![](https://raw.githubusercontent.com/TradeCraftMC/TradeCraft/refs/heads/main/src/main/resources/web/assets/banner.png)
# TradeCraft
TradeCraft is a Paper/Bukkit plugin that overhauls economy into a full-fledged system with loans, companies, orders, currency and more! It features a beautiful web UI that allows players to create, manage and sell companies, items and more on the server.

## Vision
TradeCraft is a complex plugin designed to support complex economies. As a baseline goal, TradeCraft will support both individual sellers and player-created 'companies', which can have a variety of sub-brands. These 'vendors' will be able to list goods on their storefronts, and players may buy them with a variety of currencies. Server admins set up these currencies, and assign relative values.

### Currencies
Currencies are intented to mimick actual currencies, where they are either backed with:

- Fiat (authority, essentially they are just created by a government)
- Material (based on a material, like gold)

Admins can create either kind of currency. Their values are then dynamically determined using a supply-demand model.

Players and companies have separate wallets for each currency, but they can also convert freely between them at the given exchange rate. 

### Individual sellers
Individual sellers are single players that want to sell items under their own name. They, intentionally, do not have a lot of flexibility with their branding or style. Server admins are intended to balance this out by having lower (or no) taxes on individual sellers. 

Buying and selling is tied directly to their inventory and wallet. 

### Companies
Companies are separate entities that multiple players can participate in together. They have their own inventory and wallet, which players can deposit and withdraw from in accordance with the company/server's settings. 

Companies can have multiple 'brands' that they sell under, and are given more customisation options. In order to balance this out, server admins are suggested to add a tax or setup cost to companies. 
