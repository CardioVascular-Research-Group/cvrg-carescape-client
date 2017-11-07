package edu.jhu.cvrg.carescape;

/*
Copyright 2017 Johns Hopkins University Institute for Computational Medicine

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
* @author Stephen Granite
* 
*/

import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import edu.jhu.cvrg.carescape.carescapeFacade;

import java.io.IOException;

public class Driver {

	static Options options = new Options();

	/**
	 * Registers command line interface options.
	 */
	private static void registerOptions() {
		Option version = new Option("v", "version", false, "Displays version information");

		Option echo = new Option("e", "echo", false, "Flag to echo carescape sessions");
		Option high = new Option("h", "high", false, "High frequency data capture");
		Option low = new Option("l", "low", false, "Low frequency data capture");
		Option minutes = new Option("m", "minutes", false, "Minutes to wait before writing data");
		Option room = new Option("r", "room", true, "Room to capture");

		options.addOption(version);
		options.addOption(echo);
		options.addOption(high);
		options.addOption(low);
		options.addOption(minutes);
		options.addOption(room);
		
	}

	private static void printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar cvrg-carescape-client.jar", options);
	}

	public static void main(String[] args) {

		registerOptions();

		CommandLineParser parser = new BasicParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			@SuppressWarnings("resource")
			ApplicationContext context =  new AnnotationConfigApplicationContext(ApplicationConfigs.class);

			if (cmd.hasOption("version")) {
				String versionInfo = (String)context.getBean("version");
				System.out.println(versionInfo);

			} else if (cmd.hasOption("high") || cmd.hasOption("low")) {
				
				String roomToCapture = "";
				boolean high = false, echo = false;
				int minutesToWait = 3;

				if (cmd.hasOption("room")) roomToCapture = cmd.getOptionValue("room");
				if (cmd.hasOption("minutes")) minutesToWait = new Integer(cmd.getOptionValue("minutes")).intValue();
				if (cmd.hasOption("high")) high = true;
				if (cmd.hasOption("echo")) echo = true;

				carescapeFacade facade = (carescapeFacade)context.getBean("carescapeFacade");
				facade.processRoom(roomToCapture, high, echo, minutesToWait);

			} else {
				printHelp();
			}

		} catch (ParseException | IOException | java.text.ParseException e) {
			System.err.println(e.getMessage());
		}

	}
}
