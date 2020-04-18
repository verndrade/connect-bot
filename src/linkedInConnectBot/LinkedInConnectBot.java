package linkedInConnectBot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class LinkedInConnectBot {
	private static boolean fileInput;
	private static Scanner scan;
	private static Scanner userInput;

	private static void findInput() {
		System.out.println("Would you like to use a input file? (y/n)");
		if (userInput.nextLine().toLowerCase().contains("y")) {
			System.out.println("Input File Name:");
			String file = userInput.nextLine();
			System.out.println("You provided input file " + file);

			try {
				scan = new Scanner(LinkedInConnectBot.class.getClass().getResourceAsStream("/" + file));
				fileInput = true;
				return;
			} catch (Exception e) {
				System.out.println(file + " not found");
			}
		}

		System.out.println("Using user input");
		scan = new Scanner(System.in);
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		userInput = new Scanner(System.in);

		System.out.println("Welcome to the LinkedInConnectBot!");
		System.out.println("What is your LinkedIn email?");
		String username = userInput.nextLine().toLowerCase();
		System.out.println("What is your LinkedIn password?");
		String password = userInput.nextLine();
		System.out.println("Would you like to run in headless mode (no window)? (y/n)");
		boolean headless = userInput.nextLine().toLowerCase().contains("y");

		int numThreads = 1;
		while (true) {
			System.out.println("How many threads would you like to use? (default 1, up to 5)");
			try {
				numThreads = Integer.parseInt(userInput.nextLine());
				if ((numThreads > 0) && (numThreads < 6)) {
					break;
				}
				System.out.println("That was out of range!");
			} catch (Exception e) {
				System.out.println("That was not an integer!");
			}
		}

		fileInput = false;
		if (args.length > 0) {
			System.out.println("You provided filename " + args[0]);
			try {
				scan = new Scanner(LinkedInConnectBot.class.getResourceAsStream("/" + args[0]));
				fileInput = true;
			} catch (Exception e) {
				System.out.println(args[0] + " not found");
				findInput();
			}
		} else {
			findInput();
		}

		ArrayList<ConnectThread> threads = new ArrayList<>();
		ArrayList<String> failed = new ArrayList<>();
		Semaphore lock = new Semaphore(numThreads);
		boolean done = false;

		for (int i = 0; i < numThreads; i++) {
			ConnectThread thread = new ConnectThread(username, password, headless, lock);

			if (!fileInput) {
				System.out.println("Enter a URL (stop if done)");
			}
			String input = "stop";
			if (scan.hasNext()) {
				input = scan.nextLine();
				if (input.equals("stop")) {
					done = true;
					break;
				}
				thread.setUrl(input);
			}
			else {
				done = true;
				break;
			}

			try {
				lock.acquire();
			} catch (Exception e) {
			}
			threads.add(thread);
			thread.start();
		}

		while (true) {
			if (!fileInput && !done) {
				System.out.println("Enter a URL (stop if done)");
			}

			String input = "stop";
			if (!done && scan.hasNext()) {
				input = scan.nextLine();
			}
			if (input.equals("stop")) {
				break;
			}

			try {
				lock.acquire();
			} catch (Exception e) {
			}
			for (ConnectThread thread : threads) {
				if (thread.getFinished()) {
					System.out.println(thread.getUrl() + thread.getFailed());
					if (thread.getFailed()) {
						failed.add(thread.getUrl());
					}
					thread.setUrl(input);
					break;
				}
			}

			if (fileInput && !scan.hasNext()) {
				break;
			}
		}

		for (ConnectThread thread : threads) {
			thread.setTerminated(true);
		}

		for (ConnectThread thread : threads) {
			thread.join();
		}

		for (ConnectThread thread : threads) {
			if (thread.getFailed()) {
				failed.add(thread.getUrl());
			}
		}

		if (!failed.isEmpty()) {
			System.out.println("Would you like to save failed URLs?: (y/n)");
			if (userInput.nextLine().toLowerCase().contains("y")) {
				while (true) {
					System.out.println("Output File Name: (or cancel)");
					String file = userInput.nextLine();
					if (file.equals("cancel")) {
						break;
					}

					if ((new File(file)).exists()) {
						System.out.println("File exists. Are you sure? (y/n)");
						if (!userInput.nextLine().toLowerCase().contains("y")) {
							continue;
						}
					}

					FileWriter fileWriter = new FileWriter(file, false);
					for (String failUrl : failed) {
						if (failUrl != null) {
							fileWriter.write(failUrl + "\n");
						}
					}
					fileWriter.close();
					break;
				}
			}

			System.out.println("Would you like to print failed URLs?: (y/n)");
			if (userInput.nextLine().toLowerCase().contains("y")) {
				for (String failUrl : failed) {
					if (failUrl != null) {
						System.out.println(failUrl);
					}
				}
			}
		}

		System.out.println("Thanks for using the LinkedInConnectBot!");
		if (System.getProperty("os.name").contains("Windows")) {
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
		} else {
			Runtime.getRuntime().exec("killall chromedriver.exe");
		}
		userInput.close();
		scan.close();
	}
}
