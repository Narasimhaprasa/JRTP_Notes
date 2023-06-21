package in.ashokit.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EligResponse {

	private String PlanName;
	private String planStatus;
	private LocalDate planStartDate;
	private LocalDate planEndDate;
	private Double benefitAmt;
	private String denialReason;
}
