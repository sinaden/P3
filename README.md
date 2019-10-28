# P3
AAU semester project #3

First activity is only to inster your nickname if it's a fresh installation. 
Second activity in main menu with both buttons and hamburger menu.
The WiFi symbol in main menu is updated every 10 seconds and will change depending on the strenght of the signal.
For the name of wifi to display correctly under WiFi symbol in main menu you need to allow acces to your location.
Buttons in main menu don't work, they only show toast messeges to test if they are working. Some of them have other test function described in "How to test" bellow


How to test:
1. Connect 2 phones P1 and P2
2. Open Logcat and search for "WiFi" to see what's going on
3. Launch app on P1, click on Global Chat
4. Launch app on P2, click on Friends, wait for app to find peers(can be checked on Logcat)
5. P2, click on global chat, IMPORTANT in Logcat see if P2 send log about beeing the Client, for some reason sometimes it doesn't work.
6. P2, click on Chat Roulette, there should be Toast on P1 with username of P2. Works both ways.


TODO:
1. Identification of other device with the same app in current WiFi network (easy)
2. Fragments (easy-medium)
3. Design messaging part of the app (medium)
4. Make it so users can drop in and out of app without any problems(??? probably medium-hard)
5. Think what else needs to be done XD (hard)
