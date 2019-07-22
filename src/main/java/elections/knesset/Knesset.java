package elections.knesset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import elections.party.Party;
import logging.CommonLogger;
import utils.ExceptionUtils;

public class Knesset {
	private CommonLogger LOGGER = CommonLogger.getInstance();
	private float ELECTION_THRESHOLD = 0.0325f;
	private int MEMBERS_COUNT = 120;
	private List<Party> parties = new ArrayList<>();
	private AtomicInteger totalVotes = new AtomicInteger(0);
	private static Knesset KNESSET_INSTANCE = null;
	private Map <String,Integer> partiesMembers = new HashMap<String,Integer>();
	
	private Knesset(){}
	
	public static Knesset getKnessetInstance(){
		if(KNESSET_INSTANCE == null){
			synchronized (Knesset.class) {
				if(KNESSET_INSTANCE == null){
					KNESSET_INSTANCE = new Knesset();
				}
			}
		}
		return KNESSET_INSTANCE;
	}
	
	public int getPartyMembers(String partyName){
		return partiesMembers.get(partyName) != null? partiesMembers.get(partyName):0;
	}
	
	public void clean(){
		KNESSET_INSTANCE = null;
	}
	
	public int getMembersCount() {
		return MEMBERS_COUNT;
	}

	public float getElectionThreshold() {
		return ELECTION_THRESHOLD;
	}

	public int getTotalVotes() {
		return totalVotes.get();
	}

	public int getPassedElectionThresholdPartiesVotes() {
		int totalVotes = 0;
		for (Party party : parties) {
			if(isValidForElection(party)){
				totalVotes += party.getVotes();
			}
		}
		return totalVotes;
	}
	
	public List<Party> getParties() {
		return parties;
	}

	public boolean addParty(Party party) {
		if(isValidForElection(party)){
			if(!parties.contains(party)){
				totalVotes.addAndGet(party.getVotes());
				parties.add(party);
				return true;
			}
		}
		return false;
	}

	public boolean leftoversAgreementJoin(Party party1, Party party2){
		if(party1!=null &&party2 !=null && isValidForElection(party1) && isValidForElection(party2)){
			LOGGER.log(Level.INFO,this.getClass().getName(),"leftoversAgreementJoin", "Leftover join for "+party1.getPartyName() +" and "+party2.getPartyName());
			if(!party1.isPartOfLeftoversAgreement() && !party2.isPartOfLeftoversAgreement()){
				party1.setLeftoversAgreementParty(party2);
				party2.setLeftoversAgreementParty(party1);
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidForElection(Party party){
		if(party.getVotes() == 0){
			return false;
		}
		if((party.getVotes() / (float)getTotalVotes()) > getElectionThreshold()){
			return true;
		}
		return false;
	}
	
	public void calculatePartiesMembers(){
		try{
			int knessetTotalVotes = getPassedElectionThresholdPartiesVotes();
			List <Party> parties = getPassedElectionThresholdParties();
			for (Party party : parties) {
				int members = calculateMembers(party.getVotes(), knessetTotalVotes);
				if(party.isPartOfLeftoversAgreement()){
					if(shouldHaveAdditionalMemberAfterLeftover(party.getVotes(), party.getLeftoversAgreementParty().getVotes())){
						partiesMembers.put(party.getPartyName(), members + 1);
					}
				}else{
					partiesMembers.put(party.getPartyName(), members);
				}
			}	
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,this.getClass().getName(),"calculatePartiesMembers", "Fail calculating parties members");
			LOGGER.log(Level.SEVERE,this.getClass().getName(),"calculatePartiesMembers", ExceptionUtils.getStackTrace(e));
		}
	}
	
	private List<Party> getPassedElectionThresholdParties() {
		List <Party> passedParties = new ArrayList<>();
		for (Party party : parties) {
			if(isValidForElection(party)){
				passedParties.add(party);
			}
		}	
		return passedParties;
	}

	public void printPartiesMembers(){
		calculatePartiesMembers();
		Set<String> _parties = partiesMembers.keySet();
		for (String party : _parties) {
			System.out.println("Party: "+ party +" , Members: "+partiesMembers.get(party));
		}
	}
	
	private int calculateMembers(int partyVotes, int knessetTotalVotes) {
		int knessetMembersCount = getMembersCount();
		return (int) Math.floor((partyVotes/(float)knessetTotalVotes) * knessetMembersCount);
	}
	
	private boolean shouldHaveAdditionalMemberAfterLeftover(int partyVotes, int secondPartyVotes) {
		float partyLeftover = (partyVotes/(float)totalVotes.get()) * getMembersCount() - partyVotes/totalVotes.get();
		float secondPartyLeftover = (secondPartyVotes/(float)totalVotes.get()) * getMembersCount() - secondPartyVotes/totalVotes.get();

		if(partyLeftover + secondPartyLeftover > 1){
			if(partyLeftover > secondPartyLeftover){
				return true;
			}
		}
		if(partyLeftover + secondPartyLeftover == 1){
			if(partyVotes > secondPartyVotes){
				return true;
			}
		}
		return false;
	}
}

