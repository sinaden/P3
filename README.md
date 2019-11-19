# P3
AAU semester project #3

First activity is only to inster your nickname if it's a fresh installation. 
Second activity in main menu with both buttons and hamburger menu.

Connect to a wifi. Some wifis like universities or hotels might block the DNS protocol which will be used in NSD network. 
You can make a wifi network with your phone hotspot:

1. Turn on the data in your phone and turn on the hotspot. 
2. Connect your laptop to the hotspot you just made. 
3. Turn on the "Mobile hotspot" in your laptop (Beside the Airplane mode option)
4. Turn off the data and hotspot in your phone. (The laptop hotspot will continue to be sending the wifi waves)
5. Connect your phone to the wifi network that you just made with your laptop. 

How to test:
1. Connect 2 or more phones (P1 and P2)
2. Launch app on all the phones. 
3. Look at the logs, wait until they find each other. Every 1.5 seconds you will see red log messages from the other peers as such: 
7599-7599 E/WiFi: Message: I'm: HuawiTablet,my Mac ad:50:4:b8:1a:d0:cc ,at: 0#
This is an alive signal. 
First argument is the nickname for the other peer. The second argument is its mac adress and third argument is their location in the app. (Global chat is 1, main menu is 0, Setting is 5 etc) 

5. If two device are at the same location you see the following log message :  
We are in the same room, lets chat
And you see a toast message of the other peer shown on the screen. Objective is to make it so that two peer could send message in the global chat section. (The scroll view is implemented already)


Note : To go back to main menu you can press on back button. To go to other menues you can use hamburger menu.

TODO:
2. Send the actual message (chat) instead of just sending a name
3. Design the ability to make rooms. 
4. Design the ability to send friend request.
4. Make setting (Least priority!)
