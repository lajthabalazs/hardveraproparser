package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.PostFactory;

public class PricingPostFactory implements PostFactory<PricingPost> {

	@Override
	public PricingPost getPost() {
		return new PricingPost();
	}
}