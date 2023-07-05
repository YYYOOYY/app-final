package org.edupoll.model.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="feeds")
public class Feed {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;	// 기본 키
	
	@ManyToOne()
//	@JoinColumn(name="writerId", referencedColumnName = "email")
	@JoinColumn(name="writerId")
	private User writer;	// 작성자
	
	private String description;	// 본문
	private Long viewCount;

	@OneToMany(mappedBy = "feed")
	private List<FeedAttach> attaches;
	
	

}
