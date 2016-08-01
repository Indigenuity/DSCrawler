package persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

@Entity
@Audited(withModifiedFlag=true)
public class TestEntity<T> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long testEntityId;
	
	@OneToOne
	private TestOtherEntity otherEntity;
	
	private Boolean myBoolean = true;
	private boolean myPrimitiveBoolean = true;
	
	private Integer myInteger = 1;
	private int myPrimitiveInteger = 1;

	private Long myLong = 1L;
	private long myPrimitiveLong = 1L;
	
	private Double myDouble = 1.0;
	private double myPrimitiveDouble = 1.0;
	
	private String myString = "Initial String";
	
	private Character myCharacter = 'a';
	private char myPrimitiveCharacter = 'a';
	
	private String secondString = "Initial Second String";
	
	@ElementCollection
	private List<String> myStringList = new ArrayList<String>();
	{
		myStringList.add("first surprise");
		myStringList.add("second surprise");
	}
	
//	private List<T> 
	
	public long getTestEntityId() {
		return testEntityId;
	}
	public void setTestEntityId(long testEntityId) {
		this.testEntityId = testEntityId;
	}
	public Boolean getMyBoolean() {
		return myBoolean;
	}
	public void setMyBoolean(Boolean myBoolean) {
		this.myBoolean = myBoolean;
	}
	public boolean isMyPrimitiveBoolean() {
		return myPrimitiveBoolean;
	}
	public void setMyPrimitiveBoolean(boolean myPrimitiveBoolean) {
		this.myPrimitiveBoolean = myPrimitiveBoolean;
	}
	public Integer getMyInteger() {
		return myInteger;
	}
	public void setMyInteger(Integer myInteger) {
		this.myInteger = myInteger;
	}
	public int getMyPrimitiveInteger() {
		return myPrimitiveInteger;
	}
	public void setMyPrimitiveInteger(int myPrimitiveInteger) {
		this.myPrimitiveInteger = myPrimitiveInteger;
	}
	public Long getMyLong() {
		return myLong;
	}
	public void setMyLong(Long myLong) {
		this.myLong = myLong;
	}
	public long getMyPrimitiveLong() {
		return myPrimitiveLong;
	}
	public void setMyPrimitiveLong(long myPrimitiveLong) {
		this.myPrimitiveLong = myPrimitiveLong;
	}
	public Double getMyDouble() {
		return myDouble;
	}
	public void setMyDouble(Double myDouble) {
		this.myDouble = myDouble;
	}
	public double getMyPrimitiveDouble() {
		return myPrimitiveDouble;
	}
	public void setMyPrimitiveDouble(double myPrimitiveDouble) {
		this.myPrimitiveDouble = myPrimitiveDouble;
	}
	public String getMyString() {
		return myString;
	}
	public void setMyString(String myString) {
		this.myString = myString;
	}
	public Character getMyCharacter() {
		return myCharacter;
	}
	public void setMyCharacter(Character myCharacter) {
		this.myCharacter = myCharacter;
	}
	public char getMyPrimitiveCharacter() {
		return myPrimitiveCharacter;
	}
	public void setMyPrimitiveCharacter(char myPrimitiveCharacter) {
		this.myPrimitiveCharacter = myPrimitiveCharacter;
	}
	public String getSecondString() {
		return secondString;
	}
	public void setSecondString(String secondString) {
		this.secondString = secondString;
	}
	public TestOtherEntity getOtherEntity() {
		return otherEntity;
	}
	public void setOtherEntity(TestOtherEntity otherEntity) {
		this.otherEntity = otherEntity;
	}
	public List<String> getMyStringList() {
		return myStringList;
	}
	public void setMyStringList(List<String> myStringList) {
		this.myStringList = myStringList;
	}
	
	
	
	
}
