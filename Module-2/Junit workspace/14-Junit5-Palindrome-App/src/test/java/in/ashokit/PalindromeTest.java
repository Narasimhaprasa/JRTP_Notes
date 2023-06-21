package in.ashokit;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PalindromeTest {

	@ParameterizedTest
	@ValueSource(strings = { "racecar", "liril", "madam"})
	public void isPalindromeTest(String str) {

		// creating target object
		PalindromeCheck pc = new PalindromeCheck();

		// calling target class method
		boolean actual = pc.isPalidrome(str);

		// verifying result
		assertTrue(actual);
	}
}