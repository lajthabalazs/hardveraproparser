package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.PostFactory;

public class PriceMasterPostFactory implements PostFactory<PriceMasterPost> {

	@Override
	public PriceMasterPost getPost() {
		return new PriceMasterPost();
	}
}