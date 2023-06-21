package in.ashokit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

	@Test
	public void stringToIntTest1() {
		StringUtils stu = new StringUtils();
		assertThrows(IllegalArgumentException.class, () -> stu.stringToInt(null));
	}

	@Test
	public void stringToIntTest2() {
		StringUtils stu = new StringUtils();
		assertThrows(IllegalArgumentException.class, () -> stu.stringToInt(""));
	}

	@Test
	public void stringToIntTest3() {
		StringUtils stu = new StringUtils();
		Integer value = stu.stringToInt("10");
		assertEquals(10, value);
	}
}





