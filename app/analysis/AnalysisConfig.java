package analysis;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AnalysisConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long analysisConfigId;
	
	public enum AnalysisMode {
		BLOB, PAGED, MIXED
	}
	
	@Enumerated(EnumType.STRING)
	AnalysisMode analysisMode = AnalysisMode.PAGED;
	
	protected boolean excludePageAnalysisIfPresent = false; 
	
	//************ Matching
	protected Boolean doBrandMatches = false;
	protected Boolean doMetaBrandMatches = false;
	protected Boolean doGeneralMatches = false;
	protected Boolean doSchedulerMatches= false;
	protected Boolean doWpClues = false;
	protected Boolean doWebProviderMatches = false;
	protected Boolean doWpAttributionMatches= false;
	protected Boolean doLinkTextMatches = false;
	protected Boolean doTestMatches = false;
	
	//*********** Inventory
	protected Boolean doNewInventoryPage = false;
	protected Boolean doUsedInventoryPage = false;
	protected Boolean doInventoryNumbers = false;
	protected Boolean doVehicles = false;
	protected Boolean doPrices = false;
	
	//************ Extraction
	protected Boolean extractUrls = false;
	protected Boolean extractStrings = false;
	protected Boolean extractLinks = false;
	
	//*********** CapDB scores
	protected Boolean doTitleTagScoring = false;
	protected Boolean doUrlScoring = false;
	protected Boolean doAltImageTagScore = false;
	protected Boolean doH1Score = false;
	protected Boolean doMetaDescriptionScore = false;
	
	//************ Custom
	protected Boolean doCustomText = false;
	protected Boolean doCustomDoc = false;
	
	//************ Conditional Aggregates
	public Boolean needsDoc(){
		return doLinkTextMatches 
				|| doCustomDoc 
				|| doMetaBrandMatches 
				|| doTitleTagScoring 
				|| doAltImageTagScore 
				|| doH1Score 
				|| doMetaDescriptionScore
				|| doVehicles;
	}


	public void setDoAll(){
		doMatching();
		doInventory();
		doExtraction();
		doCapDb();
	}
	
	public void doInventory(){
		this.doNewInventoryPage = true;
		this.doUsedInventoryPage = true;
		this.doInventoryNumbers = true;
		this.doVehicles = true;
		this.doPrices = true;
	}
	
	public void doMatching(){
		this.doBrandMatches = true;
		this.doMetaBrandMatches = true;
		this.doGeneralMatches = true;
		this.doSchedulerMatches = true;
		this.doWpClues = true;
		this.doWebProviderMatches = true;
		this.doWpAttributionMatches = true;
		this.doLinkTextMatches = true;
		this.doTestMatches = true;
	}
	
	public void doCapDb(){
		this.doTitleTagScoring = true;
		this.doUrlScoring = true;
		this.doAltImageTagScore = true;
		this.doH1Score = true;
		this.doMetaDescriptionScore = true;
	}
	
	public void doExtraction(){
		this.extractUrls = true;
		this.extractStrings = true;
		this.extractLinks = true;
	}
	
	public boolean getDoInventory(){
		return this.doNewInventoryPage
				&& this.doUsedInventoryPage
				&& this.doInventoryNumbers
				&& this.doVehicles
				&& this.doPrices;
	}
	
	public boolean getDoMatching(){
		return this.doBrandMatches
				&& this.doMetaBrandMatches
				&& this.doGeneralMatches
				&& this.doSchedulerMatches
				&& this.doWpClues
				&& this.doWebProviderMatches
				&& this.doWpAttributionMatches
				&& this.doLinkTextMatches
				&& this.doTestMatches;
	}
	
	public boolean getDoCapDb() {
		return this.doTitleTagScoring
				&& this.doUrlScoring
				&& this.doAltImageTagScore
				&& this.doH1Score
				&& this.doMetaDescriptionScore;
	}
	
	public boolean getDoExtraction() {
		return this.extractUrls
				&& this.extractStrings
				&& this.extractLinks;
	}

	public Boolean getDoBrandMatches() {
		return doBrandMatches;
	}

	public void setDoBrandMatches(Boolean doBrandMatches) {
		this.doBrandMatches = doBrandMatches;
	}

	public Boolean getDoMetaBrandMatches() {
		return doMetaBrandMatches;
	}

	public void setDoMetaBrandMatches(Boolean doMetaBrandMatches) {
		this.doMetaBrandMatches = doMetaBrandMatches;
	}

	public Boolean getDoGeneralMatches() {
		return doGeneralMatches;
	}

	public void setDoGeneralMatches(Boolean doGeneralMatches) {
		this.doGeneralMatches = doGeneralMatches;
	}

	public Boolean getDoSchedulerMatches() {
		return doSchedulerMatches;
	}

	public void setDoSchedulerMatches(Boolean doSchedulerMatches) {
		this.doSchedulerMatches = doSchedulerMatches;
	}

	public Boolean getDoWpClues() {
		return doWpClues;
	}

	public void setDoWpClues(Boolean doWpClues) {
		this.doWpClues = doWpClues;
	}

	public Boolean getDoWebProviderMatches() {
		return doWebProviderMatches;
	}

	public void setDoWebProviderMatches(Boolean doWebProviderMatches) {
		this.doWebProviderMatches = doWebProviderMatches;
	}

	public Boolean getDoWpAttributionMatches() {
		return doWpAttributionMatches;
	}

	public void setDoWpAttributionMatches(Boolean doWpAttributionMatches) {
		this.doWpAttributionMatches = doWpAttributionMatches;
	}

	public Boolean getDoLinkTextMatches() {
		return doLinkTextMatches;
	}

	public void setDoLinkTextMatches(Boolean doLinkTextMatches) {
		this.doLinkTextMatches = doLinkTextMatches;
	}

	public Boolean getDoNewInventoryPage() {
		return doNewInventoryPage;
	}

	public void setDoNewInventoryPage(Boolean doNewInventoryPage) {
		this.doNewInventoryPage = doNewInventoryPage;
	}

	public Boolean getDoUsedInventoryPage() {
		return doUsedInventoryPage;
	}

	public void setDoUsedInventoryPage(Boolean doUsedInventoryPage) {
		this.doUsedInventoryPage = doUsedInventoryPage;
	}

	public Boolean getDoInventoryNumbers() {
		return doInventoryNumbers;
	}

	public void setDoInventoryNumbers(Boolean doInventoryNumbers) {
		this.doInventoryNumbers = doInventoryNumbers;
	}

	public Boolean getExtractUrls() {
		return extractUrls;
	}

	public void setExtractUrls(Boolean extractUrls) {
		this.extractUrls = extractUrls;
	}

	public Boolean getExtractStrings() {
		return extractStrings;
	}

	public void setExtractStrings(Boolean extractStrings) {
		this.extractStrings = extractStrings;
	}

	public Boolean getExtractLinks() {
		return extractLinks;
	}

	public void setExtractLinks(Boolean extractLinks) {
		this.extractLinks = extractLinks;
	}

	public AnalysisMode getAnalysisMode() {
		return analysisMode;
	}

	public void setAnalysisMode(AnalysisMode analysisMode) {
		this.analysisMode = analysisMode;
	}


	public Boolean getDoTestMatches() {
		return doTestMatches;
	}

	public void setDoTestMatches(Boolean doTestMatches) {
		this.doTestMatches = doTestMatches;
	}

	public Boolean getDoCustomText() {
		return doCustomText;
	}

	public void setDoCustomText(Boolean doCustomText) {
		this.doCustomText = doCustomText;
	}

	public Boolean getDoCustomDoc() {
		return doCustomDoc;
	}

	public void setDoCustomDoc(Boolean doCustomDoc) {
		this.doCustomDoc = doCustomDoc;
	}

	public Boolean getDoTitleTagScoring() {
		return doTitleTagScoring;
	}

	public void setDoTitleTagScoring(Boolean doTitleTagScoring) {
		this.doTitleTagScoring = doTitleTagScoring;
	}

	public Boolean getDoUrlScoring() {
		return doUrlScoring;
	}

	public void setDoUrlScoring(Boolean doUrlScoring) {
		this.doUrlScoring = doUrlScoring;
	}

	public Boolean getDoAltImageTagScore() {
		return doAltImageTagScore;
	}

	public void setDoAltImageTagScore(Boolean doAltImageTagScore) {
		this.doAltImageTagScore = doAltImageTagScore;
	}

	public Boolean getDoH1Score() {
		return doH1Score;
	}

	public void setDoH1Score(Boolean doH1Score) {
		this.doH1Score = doH1Score;
	}

	public Boolean getDoMetaDescriptionScore() {
		return doMetaDescriptionScore;
	}

	public void setDoMetaDescriptionScore(Boolean doMetaDescriptionScore) {
		this.doMetaDescriptionScore = doMetaDescriptionScore;
	}

	public Boolean getDoVehicles() {
		return doVehicles;
	}

	public void setDoVehicles(Boolean doVehicles) {
		this.doVehicles = doVehicles;
	}

	public Boolean getDoPrices() {
		return doPrices;
	}

	public void setDoPrices(Boolean doPrices) {
		this.doPrices = doPrices;
	}


	public boolean isExcludePageAnalysisIfPresent() {
		return excludePageAnalysisIfPresent;
	}


	public void setExcludePageAnalysisIfPresent(boolean excludePageAnalysisIfPresent) {
		this.excludePageAnalysisIfPresent = excludePageAnalysisIfPresent;
	}
	
	
}
