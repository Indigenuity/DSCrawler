package crawling.discovery.entities;

public class SourceQualification {

	protected QualificationStatus qualificationStatus;
	protected String notes;
	
	public SourceQualification(){
		this.qualificationStatus = QualificationStatus.UNTESTED;
	}
	public SourceQualification(QualificationStatus qualificationStatus){
		this.qualificationStatus = qualificationStatus;
	}
	public SourceQualification(QualificationStatus qualificationStatus, String notes){
		this(qualificationStatus);
		this.notes = notes;
	}
	public static enum QualificationStatus{
		UNTESTED, QUALIFIED, UNQUALIFIED 
	}
	public QualificationStatus getQualificationStatus() {
		return qualificationStatus;
	}
	public void setQualificationStatus(QualificationStatus qualificationStatus) {
		this.qualificationStatus = qualificationStatus;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
