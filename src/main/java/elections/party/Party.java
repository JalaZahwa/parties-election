package elections.party;
import java.util.concurrent.atomic.AtomicInteger;

public class Party {
	private String partyName;
	private volatile AtomicInteger votes = new AtomicInteger(0);
	private Party leftoversAgreementParty = null;

	public Party(String partyName){
		this.partyName = partyName;
	}

	public boolean isPartOfLeftoversAgreement(){
		return leftoversAgreementParty==null? false:true;
	}

	public Party getLeftoversAgreementParty() {
		return leftoversAgreementParty;
	}

	public void setLeftoversAgreementParty(Party leftoversAgreementParty) {
		this.leftoversAgreementParty = leftoversAgreementParty;
	}

	public String getPartyName(){
		return partyName;
	}

	public int getVotes() {
		return votes.get();
	}

	public void vote(){
		votes.incrementAndGet();
	}

}
