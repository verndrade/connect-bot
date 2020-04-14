package linkedInConnectBot;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class LinkedInConnectBot {
	private static Actions actions;
	private static WebDriver driver;
	private static boolean fileInput;
	private static JavascriptExecutor jse;
	private static boolean mulligan;
	private static Scanner scan;
	private static Scanner userInput;

	public static void connect(String url) throws InterruptedException {
		try {
			driver.get(url);
		} catch (Exception e) {
			System.out.println("Could not load URL " + url);
			return;
		}
		System.out.println("Loading URL " + url);
		Thread.sleep(5000);
		System.out.println("Scrolling down");
		jse.executeScript("scroll(0, 250);");
		Thread.sleep(5000);
		try {
			System.out.println("Checking for Connect");
			actions.moveToElement(driver.findElement(By.xpath(
					"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/span[1]/div/button")))
					.click().perform();
			Thread.sleep(2000);
			actions.moveToElement(driver.findElement(By.xpath("/html/body/div[4]/div/div/div[3]/button[2]"))).click()
					.perform();
			System.out.println("Invitation sent to " + url);
			return;
		} catch (Exception e) {
			System.out.println("Connect Button Not Found");
		}
		System.out.println("Clicking More...");
		try {
			actions.moveToElement(driver.findElement(By.xpath(
					"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/div/artdeco-dropdown/artdeco-dropdown-trigger/button")))
					.click().perform();
		} catch (Exception e) {
			System.out.println("Could not connect to " + url);
			if (mulligan) {
				System.out.println("Trying to connect to " + url + " again");
				mulligan = false;
				connect(url);
			}
			return;
		}
		Thread.sleep(1000);
		try {
			actions.moveToElement(driver.findElement(By.xpath(
					"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/div/artdeco-dropdown/artdeco-dropdown-content/div/ul/li[4]/div")))
					.click().perform();
		} catch (Exception e) {
			System.out.println("Could not connect to " + url);
			if (mulligan) {
				System.out.println("Trying to connect to " + url + " again");
				mulligan = false;
				connect(url);
			}
			return;
		}
		Thread.sleep(5000);
		try {
			actions.moveToElement(driver.findElement(By.xpath("/html/body/div[4]/div/div/div[3]/button[2]"))).click()
					.perform();
			System.out.println("success " + url);
		} catch (Exception e) {
			System.out.println("Could not connect to " + url);
			if (mulligan) {
				System.out.println("Trying to connect to " + url + " again");
				mulligan = false;
				connect(url);
			}
			return;
		}
	}

	private static void endProgram(int exitCode) throws IOException {
		System.out.println("Thanks for using the LinkedInConnectBot!");
		if (System.getProperty("os.name").contains("Windows")) {
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		} else {
			Runtime.getRuntime().exec("killall chromedriver.exe");
		}
		System.exit(exitCode);
	}

	private static void findInput() {
		System.out.println("Would you like to use a input file? (y/n)");
		if (userInput.nextLine().toLowerCase().contains("y")) {
			System.out.println("Input File Name:");
			String file = userInput.nextLine();
			System.out.println("You provided filename " + file);
			try {
				scan = new Scanner(new File(file));
				fileInput = true;
				return;
			} catch (Exception e) {
				System.out.println(file + " not found");
			}
		}
		System.out.println("Using user input");
		scan = new Scanner(System.in);
	}

	private static void login() throws IOException {
		driver.get("https://www.linkedin.com/");
		System.out.println("Loading URL");
		try {
			System.out.println("Trying to click Sign In");
			driver.findElement(By.xpath("/html/body/nav/a[3]")).click();
			System.out.println("Trying to Log In");
			System.out.println("Input Email: ");
			driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[1]/input")).sendKeys(userInput.nextLine());
			System.out.println("Input Password: ");
			driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[2]/input")).sendKeys(userInput.nextLine());
			Thread.sleep(1000);
			try {
				driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[4]/button")).click();
			} catch (Exception e) {
				driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[3]/button")).click();
			}
			Thread.sleep(1000);
		} catch (Exception e) {

		}

		if (!driver.getCurrentUrl()
				.equals("https://www.linkedin.com/feed/?trk=guest_homepage-basic_nav-header-signin")) {
			System.out.println("Failed to login");
			driver.close();
			endProgram(2);
		}
		System.out.println("Logged In");
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

		userInput = new Scanner(System.in);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-logging"));

		System.out.println("Welcome to the LinkedInConnectBot!");
		System.out.println("Would you like to run in headless mode (no window)? (y/n)");
		if (userInput.nextLine().toLowerCase().contains("y")) {
			options.addArguments("--headless");
		}

		try {
			driver = new ChromeDriver(options);
		} catch (Exception e) {
			System.out.println(
					"Cannot find ChromeDriver. Please ensure chromedriver.exe is in the same location as this executable. Download chromedriver.exe at https://chromedriver.chromium.org/downloads");
			endProgram(1);
		}

		jse = (JavascriptExecutor) driver;
		actions = new Actions(driver);

		fileInput = false;

		if (args.length > 0) {
			System.out.println("You provided filename " + args[0]);
			try {
				scan = new Scanner(new File(args[0]));
				fileInput = true;
			} catch (Exception e) {
				System.out.println(args[0] + " not found");
				findInput();
			}
		} else {
			findInput();
		}

		login();

		String input = "";
		while (true) {
			mulligan = true;
			System.out.println("Enter a URL (stop if done)");
			input = scan.nextLine();
			if (input.equals("stop")) {
				break;
			}
			System.out.println("Entered URL: " + input);
			connect(input);
			if (fileInput && !scan.hasNext()) {
				break;
			}
		}

		driver.quit();
		endProgram(0);
	}
}