Google-Voice-Unhooked-Droid
===========================
Google voice unhooked for android attempts to allow users to initiate call-backs from their android phones. 

!!!WARNING!!!
Google voice does not have an official API! This app has been created by sniffing packets while using google voice.
In fact, the grand central session is started by making a simple GET request for https://google.com/voice and scraping the response.
So, if you get your google account deactivated for messing arround, thats your problem.

Prerequisites for using the app: 
~A google account with voice enabled
~Internet access

Limitations:
This app has been created by sniffing packets while using google voice.
In fact, the grand central session is started by making a simple GET request for https://google.com/voice and scraping the response.
So it will not work if:
-You trigger a captcha
-You use 2 step authentication

Credits:
All the code is based on the knowledge found at: 
-http://googlevoice.org/pages.php?title=sniffing 
-https://code.google.com/p/google-voice-java/
