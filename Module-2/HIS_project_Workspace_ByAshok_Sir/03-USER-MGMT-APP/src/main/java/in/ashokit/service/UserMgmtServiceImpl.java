package in.ashokit.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import in.ashokit.bindings.ActivateAccount;
import in.ashokit.bindings.Login;
import in.ashokit.bindings.User;
import in.ashokit.entity.UserMaster;
import in.ashokit.repo.UserMasterRepo;
import in.ashokit.utils.EmailUtils;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {

	private Logger logger = LoggerFactory.getLogger(UserMgmtServiceImpl.class);

	@Autowired
	private UserMasterRepo userMasterRepo;

	@Autowired
	private EmailUtils emailUtils;

	private Random random = new Random();

	@Override
	public boolean saveUser(User user) {

		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);

		entity.setPassword(generateRandomPwd());
		entity.setAccStatus("In-Active");

		UserMaster save = userMasterRepo.save(entity);

		String subject = "Your Registration Success";
		String filename = "REG-EMAIL-BODY.txt";
		String body = readEmailBody(entity.getFullname(), entity.getPassword(), filename);

		emailUtils.sendEmail(user.getEmail(), subject, body);

		return save.getUserId() != null;
	}

	@Override
	public boolean activateUserAcc(ActivateAccount activateAcc) {
		UserMaster entity = new UserMaster();
		entity.setEmail(activateAcc.getEmail());
		entity.setPassword(activateAcc.getTempPwd());

		// select * from user_master where email=? and pwd=?
		Example<UserMaster> of = Example.of(entity);

		List<UserMaster> findAll = userMasterRepo.findAll(of);

		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setPassword(activateAcc.getNewPwd());
			userMaster.setAccStatus("Active");
			userMasterRepo.save(userMaster);
			return true;
		}
	}

	@Override
	public List<User> getAllUsers() {
		List<UserMaster> findAll = userMasterRepo.findAll();
		List<User> users = new ArrayList<>();
		for (UserMaster entity : findAll) {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public User getUserById(Integer userId) {
		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if (findById.isPresent()) {
			User user = new User();
			UserMaster userMaster = findById.get();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}
		return null;
	}

	@Override
	public boolean deleteUserById(Integer userId) {
		try {
			userMasterRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			logger.error("Exception Occured", e);
		}
		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String accStatus) {
		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(accStatus);
			userMasterRepo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {
		UserMaster entity = userMasterRepo.findByEmailAndPassword(login.getEmail(), login.getPassword());

		if (entity == null) {
			return "Invalid Credentials";
		}

		if (entity.getAccStatus().equals("Active")) {
			return "SUCCESS";
		} else {
			return "Account not activated";
		}
	}

	@Override
	public String forgotPwd(String email) {
		UserMaster entity = userMasterRepo.findByEmail(email);
		if (entity == null) {
			return "Invalid Email";
		}
		String subject = "Forgot Password";
		String fileName = "RECOVER-PWD-BODY.txt";
		String body = readEmailBody(entity.getFullname(), entity.getPassword(), fileName);

		boolean sendEmail = emailUtils.sendEmail(email, subject, body);

		if (sendEmail) {
			return "Password sent to your registered email";
		}
		return null;
	}

	private String generateRandomPwd() {
		String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

		StringBuilder sb = new StringBuilder();
		int length = 6;
		for (int i = 0; i < length; i++) {
			int index = this.random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	private String readEmailBody(String fullname, String pwd, String filename) {
		String url = "";
		String mailBody = null;
		try (FileReader fr = new FileReader(filename); 
				BufferedReader br = new BufferedReader(fr);) {

			StringBuilder buffer = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				buffer.append(line);
				line = br.readLine();
			}
			mailBody = buffer.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("PWD", pwd);
		} catch (Exception e) {
			logger.error("Exception Occured", e);
		}
		return mailBody;
	}
}
