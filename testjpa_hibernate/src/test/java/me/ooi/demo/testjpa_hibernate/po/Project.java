package me.ooi.demo.testjpa_hibernate.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Entity
@Table(name="project")
public class Project {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id ; 
	
	@Column(name="name")
	private String name ;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + "]";
	} 

}
