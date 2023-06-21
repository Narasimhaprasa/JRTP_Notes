package in.ashokit.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.entity.CitizenAppEntity;
import in.ashokit.entity.CoTriggerEntity;
import in.ashokit.entity.DcCaseEntity;
import in.ashokit.entity.DcChildrenEntity;
import in.ashokit.entity.DcEducationEntity;
import in.ashokit.entity.DcIncomeEntity;
import in.ashokit.entity.EligDtlsEntity;
import in.ashokit.entity.PlanEntity;
import in.ashokit.exception.EdException;
import in.ashokit.repository.CitizenAppRepository;
import in.ashokit.repository.CoTriggerRepository;
import in.ashokit.repository.DcCaseRepo;
import in.ashokit.repository.DcChildrenRepo;
import in.ashokit.repository.DcEducationRepository;
import in.ashokit.repository.DcIncomeRepository;
import in.ashokit.repository.EligDtlsRepository;
import in.ashokit.repository.PlanRepository;
import in.ashokit.response.EligResponse;

@Service
public class EligServiceImpl implements EligService {

	@Autowired
	private DcCaseRepo dcCaseRepo;

	@Autowired
	private PlanRepository planRepo;

	@Autowired
	private DcIncomeRepository incomeRepo;

	@Autowired
	private DcChildrenRepo childRepo;

	@Autowired
	private CitizenAppRepository appRepo;

	@Autowired
	private DcEducationRepository eduRepo;

	@Autowired
	private EligDtlsRepository eligRepo;

	@Autowired
	private CoTriggerRepository coTrgRepo;

	@Override
	public EligResponse determineEligibility(Long caseNum) {
		EligResponse eligResponse = null;
		try {
			Optional<DcCaseEntity> caseEntity = dcCaseRepo.findById(caseNum);
			Integer planId = null;
			String planName = null;
			Integer appId = null;

			if (caseEntity.isPresent()) {
				DcCaseEntity dcCaseEntity = caseEntity.get();
				planId = dcCaseEntity.getPlanId();
				appId = dcCaseEntity.getAppId();
			}

			Optional<PlanEntity> planEntity = planRepo.findById(planId);
			if (planEntity.isPresent()) {
				PlanEntity plan = planEntity.get();
				planName = plan.getPlanName();
			}

			Optional<CitizenAppEntity> app = appRepo.findById(appId);
			Integer age = 0;
			CitizenAppEntity citizenAppEntity = null;
			if (app.isPresent()) {
				citizenAppEntity = app.get();
				LocalDate dob = citizenAppEntity.getDob();
				LocalDate now = LocalDate.now();
				age = Period.between(dob, now).getYears();
			}

			eligResponse = executePlanCondtions(caseNum, planName, age);

			// logic to store data in db
			EligDtlsEntity eligEntity = new EligDtlsEntity();
			BeanUtils.copyProperties(eligResponse, eligEntity);

			eligEntity.setCaseNum(caseNum);
			eligEntity.setHolderName(citizenAppEntity.getFullname());
			eligEntity.setHolderSsn(citizenAppEntity.getSsn());

			eligRepo.save(eligEntity);

			CoTriggerEntity coEntity = new CoTriggerEntity();
			coEntity.setCaseNum(caseNum);
			coEntity.setTrgStatus("Pending");

			coTrgRepo.save(coEntity);

		} catch (Exception e) {
			throw new EdException(e.getMessage());
		}
		return eligResponse;

	}

	private EligResponse executePlanCondtions(Long caseNum, String planName, Integer age) {

		EligResponse response = new EligResponse();
		response.setPlanName(planName);

		DcIncomeEntity income = incomeRepo.findByCaseNum(caseNum);

		if ("SNAP".equals(planName)) {
			Double empIncome = income.getEmpIncome();
			if (empIncome <= 300) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("High Income");
			}

		} else if ("CCAP".equals(planName)) {

			boolean ageCondition = true;
			boolean kidsCountCondition = false;

			List<DcChildrenEntity> childs = childRepo.findByCaseNum(caseNum);
			if (!childs.isEmpty()) {
				kidsCountCondition = true;
				for (DcChildrenEntity entity : childs) {
					Integer childAge = entity.getChildAge();
					if (childAge > 16) {
						ageCondition = false;
						break;
					}
				}
			}

			if (income.getEmpIncome() <= 300 && kidsCountCondition && ageCondition) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Not satisfed business rules");
			}

		} else if ("Medicaid".equals(planName)) {

			Double empIncome = income.getEmpIncome();
			Double propertyIncome = income.getPropertyIncome();

			if (empIncome <= 300 && propertyIncome == 0) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("High Income");
			}

		} else if ("Medicare".equals(planName)) {
			if (age >= 65) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Age Not Matched");
			}

		} else if ("NJW".equals(planName)) {
			DcEducationEntity educationEntity = eduRepo.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			int currYear = LocalDate.now().getYear();

			if (income.getEmpIncome() <= 0 && graduationYear < currYear) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DN");
				response.setDenialReason("Rules Not Satisifed");
			}
		}

		if (response.getPlanStatus().equals("AP")) {
			response.setPlanStartDate(LocalDate.now());
			response.setPlanEndDate(LocalDate.now().plusMonths(6));
			response.setBenefitAmt(350.00);
		}
		return response;
	}
}
