package hu.droidium.hardverapro;

public interface PostFactory<T extends Post> {
	T getPost();
}
