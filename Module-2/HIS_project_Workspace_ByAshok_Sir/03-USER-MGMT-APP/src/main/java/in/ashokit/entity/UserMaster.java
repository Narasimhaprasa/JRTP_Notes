package in.ashokit.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "USER_MASTER")
@Data
public class UserMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	private String fullname;

	private String email;

	private Long mobile;

	private String gender;

	private LocalDate dob;

	private Long ssn;

	private String password;

	private String accStatus;

	@CreationTimestamp
	@Column(name="create_date", updatable=false)
	private LocalDate createdDate;

	@UpdateTimestamp
	@Column(name="updated_date", insertable=false)
	private LocalDate updatedDate;

	private String createdBy;

	private String updatedBy;

}
