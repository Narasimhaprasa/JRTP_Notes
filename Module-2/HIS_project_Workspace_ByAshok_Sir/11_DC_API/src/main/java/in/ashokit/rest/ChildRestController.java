package in.ashokit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.ashokit.binding.ChildRequest;
import in.ashokit.binding.DcSummary;
import in.ashokit.service.DcService;

@RestController
public class ChildRestController {

	@Autowired
	private DcService dcService;

	@PostMapping("/childrens")
	public ResponseEntity<DcSummary> saveChilds(@RequestBody ChildRequest request) {

		Long caseNum = dcService.saveChildres(request);

		DcSummary summary = dcService.getSummary(caseNum);

		return new ResponseEntity<>(summary, HttpStatus.OK);
	}
}
