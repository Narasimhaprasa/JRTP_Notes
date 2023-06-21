package in.ashokit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EdExceptionHandler {

	@ExceptionHandler(value = EdException.class)
	public ResponseEntity<ExceptionInfo> handleExcep(EdException edexcep) {

		ExceptionInfo info = new ExceptionInfo();
		info.setCode("EDExCEP101");
		info.setMsg(edexcep.getMessage());

		return new ResponseEntity<>(info, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}