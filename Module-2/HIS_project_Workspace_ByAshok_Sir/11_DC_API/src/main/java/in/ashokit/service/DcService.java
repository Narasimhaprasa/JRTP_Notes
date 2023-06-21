package in.ashokit.service;

import java.util.Map;

import in.ashokit.binding.ChildRequest;
import in.ashokit.binding.DcSummary;
import in.ashokit.binding.Education;
import in.ashokit.binding.Income;
import in.ashokit.binding.PlanSelection;

public interface DcService {

	public Long loadCaseNum(Integer appId);

	public Map<Integer, String> getPlanNames();

	public Long savePlanSelection(PlanSelection planSelection);

	public Long saveIncomeData(Income income);

	public Long saveEducation(Education education);

	public Long saveChildres(ChildRequest request);
	
	public DcSummary getSummary(Long caseNumber);

}
