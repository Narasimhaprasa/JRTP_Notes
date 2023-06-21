package in.ashokit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.ashokit.binding.Income;
import in.ashokit.service.DcService;

@RestController
public class IncomeRestController {

	@Autowired
	private DcService service;

	@PostMapping("/income")
	public ResponseEntity<Long> saveIncome(Income income) {
		Long caseNum = service.saveIncomeData(income);
		return new ResponseEntity<>(caseNum, HttpStatus.CREATED);
	}
}
