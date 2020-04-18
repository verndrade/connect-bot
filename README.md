

# LinkedInConnectBot

LinkedInConnectBot is a Java command line tool that leverages the power of the Selenium WebDriver Testing Suite to automate connecting to people you know and love.

The Inspiration behind this app came from my upcoming summer internship Slack where there is a #linkedin channel where 100s of people have posted their LinkedIn URLs. Knowing that I could never go through all of them manually, I thought why don't I use some of my ample COVID19 supplied freetime to learn something new. After lots of "Inspect Element-ing" and frustration with the "static" keyword here's a very rough working implementation of the LinkedInConnectBot.

## Installation

Git clone this repository
Go into the connect-bot/run directory
(Optional) Create an input.txt file with my LinkedIn in it
Run the .jar file!
```bash
git clone https://github.com/verndrade/connect-bot.git
mv connect-bot/run
echo "https://www.linkedin.com/in/verndrade" > input.txt
java -jar linkedinbot.jar [input.txt] (Optional)
```
If you are not comfortable running a .jar file off the Internet (Duh!) you can open the project in Eclipse and run it in there
```bash
git clone https://github.com/verndrade/connect-bot.git
Eclipse > File > Open Project from File System > Directory > connect-bot > Select Folder > Finish
```
## Usage
Above all the most important thing to do is ensure that the chromedriver.exe file is in the same directory as the .jar file or in the root directory of the Eclipse project.
**Login:**
To use the bot you need to provide it with your LinkedIn username and password. This information is never stored.
```bash
Welcome to the LinkedInConnectBot!
What is your LinkedIn email?
abc@xyz.com
What is your LinkedIn password?
password:)
```


**User Input:**
There are three ways to input your prospective connections profile URLs into the program: an input file provided through the command line, an input file specified by the user, or through user input. 

**Input File through Command Line** (Note: input.txt can be named anything)
```bash
java -jar linkedinbot.jar input.txt
```

**Input File in the Program** (Note: input.txt can be named anything)
```bash
java -jar linkedinbot.jar
Welcome to the LinkedInConnectBot!
...
Would you like to use a input file? (y/n)
y
Input File Name:
input.txt
You provided filename input.txt
```

**User Input** (Note: Bot will keep going until 'stop' is typed)
```bash
Welcome to the LinkedInConnectBot!
...
Would you like to use a input file? (y/n)
n
Using user input
Loading URL
...
Enter a URL (stop if done)
https://www.linkedin.com/in/verndrade
stop
```

There is an option to run **ChromeDriver headless**-ly meaning that a Chrome window will not open and show you what the program is doing (Perfect for running in the background)
```bash
Welcome to the LinkedInConnectBot!
Would you like to run in headless mode (no window)? (y/n)
y
```

There is an also an option to choose the **Amount of Threads** (up to 5) you can run at the same time, which will improve the speed at which connections are made by allowing connections to be made in parallel instead of one at a time. 
```bash
Welcome to the LinkedInConnectBot!
How many threads would you like to use? (default 1, up to 5)
5
```
## Contributing

Pull requests are welcome. 
Please leave comments on how I could improve this and if it worked for you! I'm hoping to improve this when I have the time and hopefully parallelize some of the code. 

## Disclaimer
Use at your own risk! I am if you use this tool and it causes damages to anyone or anything!

Warning: Overuse might lead to your account being flagged for suspicious activity!
