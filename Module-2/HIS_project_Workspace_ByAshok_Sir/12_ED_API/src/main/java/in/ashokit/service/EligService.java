package in.ashokit.service;

import in.ashokit.response.EligResponse;

public interface EligService {

	public EligResponse determineEligibility(Long caseNum);

}
