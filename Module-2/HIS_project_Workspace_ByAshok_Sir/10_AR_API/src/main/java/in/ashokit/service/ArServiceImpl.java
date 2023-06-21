package in.ashokit.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import in.ashokit.binding.CitizenApp;
import in.ashokit.entity.CitizenAppEntity;
import in.ashokit.repository.CitizenAppRepository;

@Service
public class ArServiceImpl implements ArService {

	@Autowired
	private CitizenAppRepository appRepo;

	@Override
	public Integer createApplication(CitizenApp app) {

		String endpointUrl = "https://ssa-web-api.herokuapp.com/ssn/{ssn}";

		/*
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> resEntity = rt.getForEntity(endpointUrl, String.class, app.getSsn());
		String stateName = resEntity.getBody();
		*/
		
		WebClient webClient = WebClient.create();
		
		String stateName = webClient.get() // it represents GET request
									 .uri(endpointUrl, app.getSsn()) // it represents url to send req
									 .retrieve() // to retrieve response
									 .bodyToMono(String.class) // to specify response type
									 .block(); // to make synchronus call

		if ("New Jersey".equals(stateName)) {
			CitizenAppEntity entity = new CitizenAppEntity();
			BeanUtils.copyProperties(app, entity);

			entity.setStateName(stateName);
			CitizenAppEntity save = appRepo.save(entity);

			return save.getAppId();
		}
		return 0;
	}
}
