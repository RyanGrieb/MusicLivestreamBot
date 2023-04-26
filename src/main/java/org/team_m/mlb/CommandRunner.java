package org.team_m.mlb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.team_m.mlb.system.SystemInfo;

/**
 * 
 * The CommandRunner class executes system commands and retrieves their output
 * using ProcessBuilder. The command and its arguments are stored as a list of
 * strings and can be modified by adding arguments. The full command can be
 * obtained as a single string. The class also provides a way to stop and
 * terminate the process.
 */
public class CommandRunner {

	private ArrayList<Consumer<String>> commandOutputCallbacks = new ArrayList<>();

	class PipeStream extends Thread {
		InputStream is;
		OutputStream os;

		public PipeStream(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int len = 0;
			try {
				while ((len = is.read(buffer)) >= 0) {
					os.write(buffer, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Process process;

	private String command;
	private List<String> arguments;

	public CommandRunner(String command, String... args) {
		this.command = command;

		if (args.length > 0) {
			this.arguments = Arrays.asList(args);
		} else {
			this.arguments = new ArrayList<>();
		}
	}

	public void addArg(String arg) {
		arguments.add(arg);
	}

	/**
	 * Returns arguments as a single string
	 * 
	 * @return Single line string
	 */
	public String getFullArguments() {
		String args = "";
		for (String arg : arguments) {
			args += " " + arg;
		}

		return args;
	}

	public void run(boolean outputPipestream) {
		String fullCommand = command + getFullArguments();
		System.out.println(fullCommand);

		try {
			String shell = null;
			String shellArg = null;
			switch (SystemInfo.osType()) {
			case "Linux":
				shell = "/bin/sh";
				shellArg = "-c";
				break;
			case "Windows":
				shell = "cmd";
				shellArg = "/c";
				break;
			case "Mac":
				break;
			}

			ProcessBuilder processBuilder = new ProcessBuilder(shell, shellArg, fullCommand);

			this.process = processBuilder.start();

			BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			Thread thread = new Thread(() -> {
				String line;
				try {
					while ((line = inputReader.readLine()) != null) {
						// Call the function for each line of output
						for (Consumer<String> callback : commandOutputCallbacks) {
							callback.accept(line);
						}
					}
				} catch (IOException e) {
					// Handle any errors that occur while reading the output
					e.printStackTrace();
				}
			});

			// Start the thread
			thread.start();

			if (outputPipestream) {
				// NOTE: This is required for ffmpeg to run.
				PipeStream out = new PipeStream(process.getInputStream(), System.out);
				PipeStream err = new PipeStream(process.getErrorStream(), System.out);

				out.start();
				err.start();
			}

			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendStopSignal(String proccessType) {
		System.out.println("Stopping...");

		try {
			switch (proccessType) {
			case "ffmpeg":
				// Send the 'q' character to ffmpeg
				process.getOutputStream().write('q');
				process.getOutputStream().flush();

				// First, destroy the process normally
				process.destroy();

				// If the process is still running, force it to terminate
				if (process.isAlive()) {
					process.destroyForcibly();
				}

				break;

			case "yt-dlp":
				// Send the 'q' character to ffmpeg
				process.getOutputStream().write('q');
				process.getOutputStream().flush();

				// First, destroy the process normally
				process.destroy();
				process.getInputStream().close();
				process.getOutputStream().close();
				process.getErrorStream().close();

				// If the process is still running, force it to terminate
				if (process.isAlive()) {
					process.destroyForcibly();
				}
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addOutputCallback(Consumer<String> callback) {
		commandOutputCallbacks.add(callback);
	}

}
