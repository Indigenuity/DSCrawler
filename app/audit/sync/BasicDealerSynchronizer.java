package audit.sync;

import java.util.List;

import crawling.projects.BasicDealer;
import play.db.jpa.JPA;

public class BasicDealerSynchronizer extends KeyedSynchronizer<BasicDealer, BasicDealer, BasicDealer>{
	
	private List<BasicDealer> locals;
	private List<BasicDealer> remotes;
	
	public BasicDealerSynchronizer(List<BasicDealer> locals, List<BasicDealer> remotes) {
		this.locals = locals;
		this.remotes = remotes;
	}
	
	@Override
	public BasicDealer getLocal(BasicDealer remote) {
		for(BasicDealer local : locals) {
			if(local.getProjectIdentifier() != null 
					&& local.getProjectIdentifier().equals(remote.getProjectIdentifier())
					&& local.getIdentifier() != null
					&& local.getIdentifier().equals(remote.getIdentifier())){
				return local;
			}
		}
		return null;
	}

	@Override
	public BasicDealer getRemote(BasicDealer local) {
		for(BasicDealer remote : remotes) {
			if(remote.getProjectIdentifier() != null 
					&& remote.getProjectIdentifier().equals(local.getProjectIdentifier())
					&& remote.getIdentifier() != null
					&& remote.getIdentifier().equals(local.getIdentifier())){
				return remote;
			}
		}
		return null;
	}
	
	@Override
	public void update(BasicDealer local, BasicDealer remote){
		local.setName(remote.getName());
		local.setStreet(remote.getStreet());
		local.setCity(remote.getCity());
		local.setState(remote.getState());
		local.setPostal(remote.getPostal());
		local.setPhone(remote.getPhone());
		local.setWebsite(remote.getWebsite());
		local.setCustom1(remote.getCustom1());
		local.setCustom2(remote.getCustom2());
		local.setCustom3(remote.getCustom3());
		local.setCustom4(remote.getCustom4());
		local.setCustom5(remote.getCustom5());
	}
	
	@Override
	public void insert(BasicDealer remote){
		BasicDealer local = new BasicDealer();
		update(local, remote);
		JPA.em().persist(local);
	}
	
	@Override
	public void outdate(BasicDealer local){
		local.setOutdated(true);
	}

}
