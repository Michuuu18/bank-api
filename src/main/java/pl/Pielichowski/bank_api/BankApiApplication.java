package pl.Pielichowski.bank_api;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.server.PortInUseException;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class BankApiApplication {

	public static void main(String[] args) {
		if (hasExplicitServerPortArg(args) || isProductionProfile()) {
			SpringApplication.run(BankApiApplication.class, args);
			return;
		}

		int[] candidates = portCandidates();

		for (int i = 0; i < candidates.length; i++) {
			int port = candidates[i];
			try {
				SpringApplication.run(BankApiApplication.class, withServerPortArg(args, port));
				return;
			} catch (Exception ex) {
				if (!causedByPortInUse(ex) || i == candidates.length - 1) {
					throw wrap(ex);
				}
				System.err.println("Port " + port + " jest zajety; probuje port " + candidates[i + 1] + "...");
			}
		}
	}

	private static boolean hasExplicitServerPortArg(String[] args) {
		for (String a : args) {
			if (a.startsWith("--server.port=")) {
				return true;
			}
		}
		return false;
	}

	private static boolean isProductionProfile() {
		String env = System.getenv("SPRING_PROFILES_ACTIVE");
		if (env != null && env.toLowerCase().contains("prod")) {
			return true;
		}
		String prop = System.getProperty("spring.profiles.active");
		return prop != null && prop.toLowerCase().contains("prod");
	}

	private static int[] portCandidates() {
		String env = System.getenv("SERVER_PORT");
		if (env != null && !env.isBlank()) {
			try {
				int first = Integer.parseInt(env.trim());
				return new int[] {first, 8082, 8222, 0};
			} catch (NumberFormatException ignored) {
				// ignore invalid SERVER_PORT
			}
		}
		return new int[] {8081, 8082, 8222, 0};
	}

	private static String[] withServerPortArg(String[] args, int port) {
		List<String> list = new ArrayList<>(Arrays.asList(args));
		list.removeIf(a -> a.startsWith("--server.port="));
		list.add("--server.port=" + port);
		return list.toArray(String[]::new);
	}

	private static boolean causedByPortInUse(Throwable ex) {
		for (Throwable cur = ex; cur != null; cur = cur.getCause()) {
			if (cur instanceof PortInUseException) {
				return true;
			}
			if (cur instanceof BindException) {
				String m = cur.getMessage();
				if (m != null && m.contains("already in use")) {
					return true;
				}
			}
		}
		return false;
	}

	private static RuntimeException wrap(Exception ex) {
		if (ex instanceof RuntimeException rex) {
			return rex;
		}
		return new IllegalStateException(ex);
	}
}
