package com.pst.support.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(nullable = false)
	private String content;
	
	@Column(columnDefinition = "BOOLEAN DEFAULT false")
	private boolean isFile;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp date = new Timestamp(new Date().getTime());
	
	@Column(nullable = false)
	private String envoyePar;
	
	@Column(nullable = false)
	private Long ticketId;
	
	public void setIsFile() {
		this.isFile = true;
	}
	
	
	public void setContentOrFile(String content, MultipartFile file) {
		if(Objects.nonNull(content) && !content.isEmpty()) {
			this.content = content;
		}
		else if(Objects.nonNull(file)) {
			this.setIsFile();
		}
	}
}
