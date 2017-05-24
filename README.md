# KrimSale

## KrimSale Minecraft Bukkit Auction Plugin

Ever wanted to have an auction house like you've seen in many mmorpgs? Just try this plugin. This is a complete reprogramming of the Guildwars 2 Auction System.  

    Build for www.worldofminecraft.de - Brauhaus der Hoffnung - s.minecraft.name:25560  
    Jenkins Builds: http://jk.wmchris.de  
    Configuration for BrauTec/TekkitLite/BukkitForge: http://wiki.minecraft.name/index.php?title=KrimSale:BrauTec   
  
Features  
  
* Offer your items - just tell the plugin how much you want - and it's done
* Buy items - just tell the plugin how many items you want and how much you're willing to pay. The plugin will find the perfect solution for you
* Request items - tell the plugin, how much you will pay for an item, if someone sells it - you'll get it
* SQL based
* Post System - you can get items even when you're not online
* Anonymous trade - you will never know, who offers the blocks
* Fee's for transactions - can be disabled
* automatic pruning
* Unlimited buy/sell options for admins (to use it as a normal shop)
* Optional location based system - for real auction houses
* Can be used with tekkit/brautec servers 

```
Commands

    auction:
         description: KrimSale Command
         usage: /auction params
    ks:
         description: KrimSale Admin
         usage: /ks params

Perms

   ks.buy:
        description: User permissions for buy
        default: op
    ks.sell:
        description: User permissions for sell
        default: op
    ks.sign:
        description: User permissions to create signs
        default: op
    ks.list:
        description: User permissions for list
        default: op
    ks.admin:
        description: User permissions for administration
        default: op
    ks.superadmin:
        description: User permissions for superadministration (KS-Admin)
        default: op
```

Video Tutorial (GERMAN): https://www.youtube.com/watch?v=JbOubi1WCX8
