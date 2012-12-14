package de.bdh.ks;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commander implements CommandExecutor {
	
	Main plugin;
	public Commander(Main plugin)
	{
		this.plugin = plugin;
	}

	public boolean enderChestClose(CommandSender s)
	{
		if(configManager.ender == 0)
			return true;
		
		int rad = 5;
		Block temp;
		if(s instanceof Player)
		{
			Player p = (Player)s;
			Block b = p.getLocation().getBlock();
			for(int i$ = (rad * -1); i$ < rad; i$++)
	        {
	        	for(int j$ = (rad * -1); j$ < rad; j$++)
	            {
	        		for(int k$ = (rad * -1); k$ < rad; k$++)
	        		{
	        			temp = b.getRelative(i$, j$, k$);
	        			if(temp.getType() == Material.ENDER_CHEST)
	        				return true;
	        		} 
	            }
	        }
		}
		return false;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[])
    {
		if(sender instanceof Player)
        {
        	if(command.getName().equals("auction"))
        	{
        		if(args.length == 0)
        		{
        			sender.sendMessage("USAGE: /auction REQUEST/SELL/BUY/DETAIL/LIST/COLLECT/ABORT");
        		} else
        		{
        			//Min 1 Parameter
        			if(args[0].equalsIgnoreCase("abort"))
        			{
        				if(args.length < 2)
                		{
        					sender.sendMessage("USAGE: /auction abort ID - you can get the id by using list");
                		} else
                		{
                			int id = 0;
                			try
            				{
            					id = Integer.parseInt(args[1]);
            				}
            				catch(Exception e) 
            				{ 
            					sender.sendMessage("ID must be numeric");
            					return true;
            				}
                			if(Main.helper.removeAuction(id,(Player)sender))
                			{
                				sender.sendMessage("Your auction has been cancelled. You can pick it up at the auction house");
                			} else 
                				sender.sendMessage("This ID was invalid or you dont have the permissions to do that");

                		}
        			}
        			else if(args[0].equalsIgnoreCase("list"))
        			{
        				int page = 1;
        				try
        				{
        					page = Integer.parseInt(args[1]);
        				}
        				catch(Exception e) { }
        				
        				
        				int amount = Main.helper.getOfferAmountFromPlayer(sender.getName());
        				int maxpage = amount / 5;
        				if(amount == 0)
        				{
        					sender.sendMessage("You don't have items for sale");
        				} else
        					sender.sendMessage("You've "+amount+" transactions. Page: "+page+" of "+maxpage);
        				
        				page = page -1;
        				page = page * 5;
        				Map<Integer,KSOffer> l = Main.helper.getOffersFromPlayer(sender.getName(),5,page);
        				for(Map.Entry<Integer, KSOffer> e: l.entrySet())
        				{
        					sender.sendMessage("ID: "+e.getKey()+ " - Block: "+KrimBlockName.getNameByItemStack(e.getValue().getItemStack()) + " Amount: "+e.getValue().getAmount()+ " for "+e.getValue().getFullPrice()+ " "+Main.econ.currencyNamePlural());
        				}
        				
        				
        			}
        			else if(args[0].equalsIgnoreCase("sell"))
        			{
        				if(!sender.hasPermission("ks.sell"))
        				{
        					sender.sendMessage("You're not allowed to sell stuff");
        					return true;
        				}
        				
        				if(this.enderChestClose(sender) == false)
    					{
        					sender.sendMessage("You've to go to an auction house to sell items");	
    					}
        				
        				//VERKAUFE
        				if(args.length < 2)
                		{
        					sender.sendMessage("USAGE: /auction sell BLOCK PRICEPERBLOCK (AMOUNT) OR /auction sell PRICE for Item in Hand");
                		} else
                		{
                			ItemStack i = null;
                			int price = 0;
                			int maxAm = 0;
                			
                			//Verkaufe Item in der Hand
                			if(args.length == 2) 
                			{
                				try
                				{
                					price = Integer.parseInt(args[1]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Price must be Numeric");
                					return true;
                				}
                				
                				i = ((Player) sender).getItemInHand();

                			//Verkaufe Gegenstand aus dem Chat
                			} else if(args.length > 2)
                			{
                				
                				if(args.length == 4)
                				{
	                				try
	                				{
	                					maxAm = Integer.parseInt(args[3]);
	                				}
	                				catch(Exception e)
	                				{
	                					sender.sendMessage("Amount must be Numeric");
	                					return true;
	                				}
                				} else maxAm = 999999;
                				
                				try
                				{
                					price = Integer.parseInt(args[2]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Price must be Numeric");
                					return true;
                				}
                				i = KrimBlockName.parseName(args[1]);
                				
                			}
                			
                			if(i == null)
            				{
            					sender.sendMessage("Block with Name/ID '"+args[1]+"' not found");
            					return true;
            				}
                			
                			if(maxAm != 0)
                				i.setAmount(maxAm);
                			
            				int am = Main.helper.removeItemsFromPlayer((Player) sender, i, i.getAmount());
            				if(am <= 0)
            				{
            					sender.sendMessage("You dont own that item");
            				} else
            				{
                				KSOffer of = new KSOffer(i,sender.getName(),price,am);
                				if(of.payFee() == false)
                				{
                					sender.sendMessage("You cannot afford the fee of "+of.getFee()+ " "+ Main.econ.currencyNamePlural());
                					Main.helper.giveBack(of);	
                				} else
                				{
	                				if(Main.helper.enlistItem(of) == true)
	                				{
	                					sender.sendMessage("Success. You're offering "+am+" Blocks for "+of.getFullPrice()+" "+Main.econ.currencyNamePlural());
	                					if(of.getFee() > 0)
	                					{
	                						sender.sendMessage("You've paid an auction-fee of '"+of.getFee()+"' "+Main.econ.currencyNamePlural());
	                					}
	                				}
	                				else
	                				{
	                					Main.helper.giveBack(of);
	                					sender.sendMessage("Something went wrong");
	                				}
                				}
            				}
                		}
        			} else if(args[0].equalsIgnoreCase("request"))
        			{
        				if(!sender.hasPermission("ks.buy"))
        				{
        					sender.sendMessage("You're not allowed to buy stuff");
        					return true;
        				}
        				
        				if(this.enderChestClose(sender) == false)
    					{
        					sender.sendMessage("You've to go to an auction house to request items");	
    					}
        				
        				if(args.length < 3)
                		{
        					sender.sendMessage("USAGE: /auction request (BLOCK) AMOUNT MAXPRICE");
                		} else 
                		{
                			int price=0, amount=0;
                			ItemStack i = null;
                			//Requeste aus Hand
                			if(args.length == 3)
	                		{
	            				try
	            				{
	            					amount = Integer.parseInt(args[1]);
	            				}
	            				catch(Exception e)
	            				{
	            					sender.sendMessage("Amount must be Numeric");
	            					return true;
	            				}
	            				try
	            				{
	            					price = Integer.parseInt(args[2]);
	            				}
	            				catch(Exception e)
	            				{
	            					sender.sendMessage("Price must be Numeric");
	            					return true;
	            				}
	            				
	            				i = ((Player) sender).getItemInHand().clone();
	            			//Requeste aus Chat
	                		} else if(args.length == 4)
	                		{
	                			try
                				{
                					amount = Integer.parseInt(args[2]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Amount must be Numeric");
                					return true;
                				}
                				try
                				{
                					price = Integer.parseInt(args[3]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Price must be Numeric");
                					return true;
                				}
                				i = KrimBlockName.parseName(args[1]);
	                		}
                			
                			if(i == null)
            				{
            					sender.sendMessage("Item '"+args[1]+"' not found");
            					return true;
            				}
            				i.setAmount(amount);
            				int bought = Main.helper.buyItems(i, price, sender.getName());
            				if(bought == -1)
            				{
            					sender.sendMessage("You dont have enough money");
            				}
            				else if(bought == amount)
            				{
            					sender.sendMessage("You've instantly bought the amount you wanted");
            				} else
            				{
            					if(bought > 0)
            						sender.sendMessage("You've instantly bought "+bought+" of "+amount);
            					int req = amount - bought;
            					
            					
            					KSOffer o = new KSOffer(i,sender.getName(),price);
            					int resp = Main.helper.enlistRequest(o);
            					if(resp == 1)
            					{
            						sender.sendMessage("You've requested "+req+" items for "+(req*price)+" "+Main.econ.currencyNamePlural());
            						sender.sendMessage("Your request is valid for 14 days. If noone offers this item for your price, you'll get your money back");
            					} else if(resp == -2)
            					{
            						sender.sendMessage("You're not allowed to request this item");
            					} else if(resp == -1)
            					{
            						sender.sendMessage("You dont have enough money");
            					}
            				}
                		}
        			} else if(args[0].equalsIgnoreCase("buy"))
        			{
        				if(!sender.hasPermission("ks.buy"))
        				{
        					sender.sendMessage("You're not allowed to buy stuff");
        					return true;
        				}
        				
        				//KAUFE
        				if(args.length < 3)
                		{
        					sender.sendMessage("USAGE: /auction buy (BLOCK) AMOUNT MAXPRICE");
                		} else
                		{
                			if(this.enderChestClose(sender) == false)
        					{
            					sender.sendMessage("You've to go to an auction house to buy items");	
            					return true;
        					}
                			
                			int price=0, amount=0;
                			ItemStack i = null;
                			
                			//Kaufe Gegenstand in der Hand
                			if(args.length == 3)
                			{
                				try
                				{
                					amount = Integer.parseInt(args[1]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Amount must be Numeric");
                					return true;
                				}
                				try
                				{
                					price = Integer.parseInt(args[2]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Price must be Numeric");
                					return true;
                				}
                				
                				//Block == IteminHand
                				i = ((Player) sender).getItemInHand().clone();
                			//Kaufe Gegenstand aus dem Chat
                			} else if(args.length == 4)
                			{
                				//Normale Usage
                				
                				try
                				{
                					amount = Integer.parseInt(args[2]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Amount must be Numeric");
                					return true;
                				}
                				try
                				{
                					price = Integer.parseInt(args[3]);
                				}
                				catch(Exception e)
                				{
                					sender.sendMessage("Price must be Numeric");
                					return true;
                				}
                				i = KrimBlockName.parseName(args[1]);
                			} 
                			
            				if(i == null)
            				{
            					sender.sendMessage("Item '"+args[1]+"' not found");
            					return true;
            				}
            				i.setAmount(amount);
            				int bought = Main.helper.buyItems(i, price, sender.getName());
            				if(bought == -1)
            				{
            					sender.sendMessage("You dont have enough money");
            				}
            				else if(bought == amount)
            				{
            					sender.sendMessage("You've bought the amount you wanted");
            				} else if(bought == 0)
            				{
            					sender.sendMessage("There is no offer which fulfills your options");
            				} else
            				{
            					sender.sendMessage("You've only bought "+bought+"/"+amount);
            				}
                		}
        			} else if(args[0].equalsIgnoreCase("detail"))
        			{
        				if(!sender.hasPermission("ks.list"))
        				{
        					sender.sendMessage("You're not allowed to list stuff");
        					return true;
        				}
        				
        				//ZEIGE
        				if(args.length < 2)
                		{
        					sender.sendMessage("USAGE: /auction detail BLOCK");
                		} else
                		{
                			ItemStack i = KrimBlockName.parseName(args[1]);
                			if(i != null)
                				Main.helper.sendInfos((Player)sender, i);
                			else
                				sender.sendMessage("ERROR: BlockID '"+args[1]+"' invalid");	
                		}
        			} else if(args[0].equalsIgnoreCase("collect"))
        			{
        				if(!sender.hasPermission("ks.buy"))
        				{
        					sender.sendMessage("You're not allowed to buy stuff");
        					return true;
        				} else
        				{
        					int am = Main.helper.hasDelivery((Player)sender);
        					if(am == 0)
        						sender.sendMessage("There is nothing for delivery");
        					/*else
        						sender.sendMessage("You've '"+am+"' items waiting for delivery");*/
        					
        					if(am > 0)
        					{
	        					if(this.enderChestClose(sender))
	        					{
	        						Main.helper.getDelivery((Player)sender);
	        					} else
	        					{
	        						sender.sendMessage("You've to go to the auction house to collect your items");
	        					}
        					}
        				}
        			}
        		}
        	}
        }
		
		return true;
    }
}
