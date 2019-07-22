package citizen;
import elections.party.Party;

public class Citizen {
	
	public Citizen(){}
	
	/**
	 * Votes to the received party
	 * @param party
	 */
	public void vote(Party party){
		party.vote();
	}
}
