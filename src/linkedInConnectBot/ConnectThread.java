package linkedInConnectBot;

import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class ConnectThread extends Thread {

	private Actions actions;
	private ChromeDriver driver;
	private boolean failed;
	private boolean finished;
	private boolean headless;
	private JavascriptExecutor jse;
	private Semaphore lock;
	private boolean loggedIn;
	private String password;
	private boolean terminated;
	private String url;
	private boolean running;
	private String username;

	public ConnectThread(String username, String password, boolean headless, Semaphore lock) {
		this.lock = lock;
		this.headless = headless;
		this.username = username;
		this.password = password;
		failed = true;
		terminated = false;
		running = true;
		finished = false;
		loggedIn = false;
	}

	public boolean getFailed() {
		return failed;
	}

	public boolean getFinished() {
		return finished;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void run() {
		while (running) {
			String exitMessage = "";
			boolean next = true;
			if (!loggedIn) {
				System.out.println("Logging In");

				System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
				System.setProperty("webdriver.chrome.silentOutput", "true");
				java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-logging"));
				if (headless) {
					options.addArguments("--headless");
				}
				options.addArguments("--window-size=1920,1080");
				driver = new ChromeDriver(options);
				actions = new Actions(driver);
				jse = driver;

				try {
					driver.get("https://www.linkedin.com/");
					driver.findElement(By.xpath("/html/body/nav/a[3]")).click();

					Thread.sleep(2000);
					driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[1]/input")).sendKeys(username);
					driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[2]/input")).sendKeys(password);
					Thread.sleep(2000);
					try {
						driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[4]/button")).click();
					} catch (Exception e) {
						driver.findElement(By.xpath("/html/body/div/main/div[2]/form/div[3]/button")).click();
					}
					Thread.sleep(2000);
				} catch (Exception e) {
				}

				try {
					if (!driver.getCurrentUrl()
							.equals("https://www.linkedin.com/feed/?trk=guest_homepage-basic_nav-header-signin")) {
						next = false;
						exitMessage = "Failed to login " + url;
					}
					System.out.println("Logged In");
				} catch (Exception e) {
					next = false;
					exitMessage = "Could not load URL " + url;
				}
			}

			if (next) {
				loggedIn = true;
				try {
					System.out.println("Loading " + url);
					driver.get(url);
					Thread.sleep(3000);
					jse.executeScript("scroll(0, 250);");
					Thread.sleep(2000);
				} catch (Exception e) {
					next = false;
					exitMessage = "Could not scroll on " + url;
				}
			}

			if (next) {
				try {
					actions.moveToElement(driver.findElement(By.xpath(
							"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/span[1]/div/button")))
							.click().perform();
					Thread.sleep(2000);
					actions.moveToElement(driver.findElement(By.xpath("/html/body/div[4]/div/div/div[3]/button[2]")))
							.click().perform();
					next = false;
					failed = false;
					exitMessage = "Invitation sent to " + url;
				} catch (Exception e) {
				}
			}

			if (next) {
				try {
					actions.moveToElement(driver.findElement(By.xpath(
							"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/div/artdeco-dropdown/artdeco-dropdown-trigger/button")))
							.click().perform();
					Thread.sleep(1000);
					actions.moveToElement(driver.findElement(By.xpath(
							"/html/body/div[5]/div[3]/div[3]/div/div/div/div/div[2]/main/div[1]/section/div[2]/div[1]/div[2]/div/div/div/artdeco-dropdown/artdeco-dropdown-content/div/ul/li[4]/div/artdeco-dropdown-item")))
							.click().perform();
					Thread.sleep(2000);
					actions.moveToElement(driver.findElement(By.xpath("/html/body/div[4]/div/div/div[3]/button[2]")))
							.click().perform();
					next = false;
					exitMessage = "Invitation sent to " + url;
					failed = false;
				} catch (Exception e) {
					next = false;
					exitMessage = "Could not connect to " + url;
				}
			}

			System.out.println(exitMessage);
			String oldUrl = url;
			finished = true;
			lock.release();
			while (finished && url.equals(oldUrl)) {
				if (terminated) {
					running = false;
					break;
				}

				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
			}
		}

		System.out.println("done");
		if (driver != null) {
			driver.quit();
		}
	}

	public void setTerminated(boolean b) {
		terminated = b;
	}

	public void setUrl(String url) {
		this.url = url;
		failed = true;
		finished = false;
	}
}
