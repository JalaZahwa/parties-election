package elections.test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import citizen.Citizen;
import elections.knesset.Knesset;
import elections.party.Party;

public class KnessetsTest {

	public static List<Party> createParties(Knesset knesset, int count){
		List<Party> parties = new ArrayList<Party>();
		for(int i=0;i<count;i++){
			parties.add(new Party("party_"+i));
		}
		return parties;
	}

	private List<Citizen> createCitizens(int count) {
		List<Citizen> citizens = new ArrayList<Citizen>();
		for(int i=0;i<count;i++){
			citizens.add(new Citizen());
		}
		return citizens;
	}

	private static void startVoting(List<Citizen> citizens, Party party) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for (Citizen citizen : citizens) {
			executor.submit(() -> citizen.vote(party));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void LeftoverJoinTest() {
		System.out.println("\n----- LeftoverJoinTest -----");
		Knesset knesset = Knesset.getKnessetInstance();
		List<Party> parties = createParties(knesset, 3);

		startVoting(createCitizens(1000),parties.get(0));
		startVoting(createCitizens(520),parties.get(1));
		startVoting(createCitizens(525),parties.get(2));
		for (Party party : parties) {
			knesset.addParty(party);
			knesset.addParty(party);
		}
		System.out.println("Knesset members before leftover agreement:");
		knesset.printPartiesMembers();

		assertEquals(knesset.getPartyMembers("party_0"), 58);
		assertEquals(knesset.getPartyMembers("party_1"), 30);
		assertEquals(knesset.getPartyMembers("party_2"), 30);

		assertFalse(knesset.leftoversAgreementJoin(null, parties.get(2)));
		assertFalse(knesset.leftoversAgreementJoin(parties.get(0), null));

		assertTrue( knesset.leftoversAgreementJoin(parties.get(1), parties.get(2)));
		assertFalse(knesset.leftoversAgreementJoin(parties.get(0), parties.get(2)));

		System.out.println("Knesset members after leftover agreement:");
		knesset.printPartiesMembers();

		assertEquals(knesset.getPartyMembers("party_0"), 58);
		assertEquals(knesset.getPartyMembers("party_1"), 30);
		assertEquals(knesset.getPartyMembers("party_2"), 31);

		assertEquals(knesset.getTotalVotes(), 2045);

		knesset.clean();
	}

	@Test
	public void partiesNotPassingElectionThresholdTest() {
		System.out.println("\n----- partiesNotPassingElectionThresholdTest -----");

		Knesset knesset = Knesset.getKnessetInstance();
		List<Party> parties = createParties(knesset, 100);

		startVoting(createCitizens(1000),parties.get(1));
		startVoting(createCitizens(1000),parties.get(2));
		startVoting(createCitizens(1000),parties.get(3));
		startVoting(createCitizens(1000),parties.get(4));
		startVoting(createCitizens(50),parties.get(5));

		for (Party party : parties) {
			knesset.addParty(party);
		}

		System.out.println("Knesset members:");
		knesset.printPartiesMembers();

		assertEquals(knesset.getPartyMembers("party_1"), 30);
		assertEquals(knesset.getPartyMembers("party_2"), 30);
		assertEquals(knesset.getPartyMembers("party_3"), 30);
		assertEquals(knesset.getPartyMembers("party_4"), 30);
		assertEquals(knesset.getPartyMembers("party_5"), 0);// didn't pass the election threshold

		knesset.clean();
	}

}
