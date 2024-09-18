package com.pst.support.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pst.support.exception.ArgumentInvalidException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp date = new Timestamp(new Date().getTime());

	@Column(nullable = false)
	private String creerPar;
	
	@JsonProperty(value = "viewMessages")
	public String consult() {
		return "/api/support/ticket/" + id;
	}

	public static boolean isIdValid(Long id) {
		return id != -1;
	}

	public void setTitle(String title) throws ArgumentInvalidException {
		if (titleIsValid(title)) {
			this.title = title;
		}
	}

	private boolean titleIsValid(String title) throws ArgumentInvalidException {
		if (Objects.isNull(title) || title.isEmpty()) {
			throw new ArgumentInvalidException("Le titre est manquant ou non conforme");
		}
		return true;
	}

	public void setCreerPar(String creerPar) throws ArgumentInvalidException {
		if (creerParIsValid(creerPar)) {
			this.creerPar = creerPar;
		}
	}

	private boolean creerParIsValid(String creerPar) throws ArgumentInvalidException {
		var regexPattern = "^(.+)@(\\S+)$";

		if (Objects.isNull(creerPar) || !Pattern.compile(regexPattern).matcher(creerPar).matches()) {
			throw new ArgumentInvalidException("L'adresse mail est manquante ou non conforme");

		}
		return true;
	}

}
