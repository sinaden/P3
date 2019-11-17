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
3. Look at the logs, wait until they find each other. 
4. Click on global chat in all the phones. (Starts to listen as a server)
4. Click on Friends in all the phones (Becomes a client to the other ones)
5. click on Chat Roulette in P1, there should be a log messages (name of the P1 device user) appears on the other phones' logcat. 


TODO:
1. Make the server and client function becomes automatical. (So the users don't need to explicitly do anything to establish connection)
2. Send the actual message (chat) instead of just sending a name
3. Design messaging part of the app (medium)
