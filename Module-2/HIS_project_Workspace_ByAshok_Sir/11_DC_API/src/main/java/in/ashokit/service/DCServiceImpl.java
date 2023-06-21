package in.ashokit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.binding.Child;
import in.ashokit.binding.ChildRequest;
import in.ashokit.binding.DcSummary;
import in.ashokit.binding.Education;
import in.ashokit.binding.Income;
import in.ashokit.binding.PlanSelection;
import in.ashokit.entity.CitizenAppEntity;
import in.ashokit.entity.DcCaseEntity;
import in.ashokit.entity.DcChildrenEntity;
import in.ashokit.entity.DcEducationEntity;
import in.ashokit.entity.DcIncomeEntity;
import in.ashokit.entity.PlanEntity;
import in.ashokit.repository.CitizenAppRepository;
import in.ashokit.repository.DcCaseRepo;
import in.ashokit.repository.DcChildrenRepo;
import in.ashokit.repository.DcEducationRepository;
import in.ashokit.repository.DcIncomeRepository;
import in.ashokit.repository.PlanRepository;

@Service
public class DCServiceImpl implements DcService {

	@Autowired
	private DcCaseRepo dcCaseRepo;

	@Autowired
	private PlanRepository planRepo;

	@Autowired
	private DcIncomeRepository incomeRepo;

	@Autowired
	private DcEducationRepository eduRepo;

	@Autowired
	private DcChildrenRepo childRepo;

	@Autowired
	private CitizenAppRepository appRepo;

	@Override
	public Long loadCaseNum(Integer appId) {

		Optional<CitizenAppEntity> app = appRepo.findById(appId);

		if (app.isPresent()) {
			DcCaseEntity entity = new DcCaseEntity();
			entity.setAppId(appId);

			entity = dcCaseRepo.save(entity);

			return entity.getCaseNum();
		}

		return 0l;
	}

	@Override
	public Map<Integer, String> getPlanNames() {

		List<PlanEntity> findAll = planRepo.findAll();

		Map<Integer, String> plansMap = new HashMap<>();

		for (PlanEntity entity : findAll) {
			plansMap.put(entity.getPlanId(), entity.getPlanName());
		}
		return plansMap;
	}

	@Override
	public Long savePlanSelection(PlanSelection planSelection) {

		Optional<DcCaseEntity> findById = dcCaseRepo.findById(planSelection.getCaseNum());

		if (findById.isPresent()) {
			DcCaseEntity dcCaseEntity = findById.get();
			dcCaseEntity.setPlanId(planSelection.getPlanId());
			dcCaseRepo.save(dcCaseEntity);

			return planSelection.getCaseNum();
		}
		return null;
	}

	@Override
	public Long saveIncomeData(Income income) {

		DcIncomeEntity entity = new DcIncomeEntity();
		BeanUtils.copyProperties(income, entity);

		incomeRepo.save(entity);

		return income.getCaseNum();
	}

	@Override
	public Long saveEducation(Education education) {

		DcEducationEntity entity = new DcEducationEntity();

		BeanUtils.copyProperties(education, entity);

		eduRepo.save(entity);

		return education.getCaseNum();
	}

	@Override
	public Long saveChildres(ChildRequest request) {
		
		List<Child> childs = request.getChilds();
		Long caseNum = request.getCaseNum();

		for (Child c : childs) {
			DcChildrenEntity entity = new DcChildrenEntity();
			BeanUtils.copyProperties(c, entity);
			entity.setCaseNum(caseNum);
			childRepo.save(entity);
		}
		
		//childRepo.saveAll(entities);
		
		return request.getCaseNum();
	}

	@Override
	public DcSummary getSummary(Long caseNum) {

		String planName = "";

		DcIncomeEntity incomeEntity = incomeRepo.findByCaseNum(caseNum);
		DcEducationEntity educationEntity = eduRepo.findByCaseNum(caseNum);
		List<DcChildrenEntity> childsEntity = childRepo.findByCaseNum(caseNum);

		Optional<DcCaseEntity> dcCase = dcCaseRepo.findById(caseNum);
		if (dcCase.isPresent()) {
			Integer planId = dcCase.get().getPlanId();
			Optional<PlanEntity> plan = planRepo.findById(planId);
			if (plan.isPresent()) {
				planName = plan.get().getPlanName();
			}
		}

		// set the data to summary obj

		DcSummary summary = new DcSummary();
		summary.setPlanName(planName);

		Income income = new Income();
		BeanUtils.copyProperties(incomeEntity, income);
		summary.setIncome(income);

		Education edu = new Education();
		BeanUtils.copyProperties(educationEntity, edu);
		summary.setEducation(edu);

		List<Child> childs = new ArrayList<>();
		for (DcChildrenEntity entity : childsEntity) {
			Child ch = new Child();
			BeanUtils.copyProperties(entity, ch);
			childs.add(ch);
		}
		summary.setChilds(childs);

		return summary;
	}

}
