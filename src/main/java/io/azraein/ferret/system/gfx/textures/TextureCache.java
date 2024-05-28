package io.azraein.ferret.system.gfx.textures;

import java.util.HashMap;
import java.util.Map;

import io.azraein.ferret.interfaces.Disposable;

public class TextureCache implements Disposable {

	public static final String DEFAULT_TEXTURE = "src/main/resources/textures/default.png";

	private Map<String, Texture> textureMap;

	public TextureCache() {
		textureMap = new HashMap<>();
		textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
	}

	public Texture createTexture(String texturePath) {
		return textureMap.computeIfAbsent(texturePath, Texture::new);
	}

	public Texture getTexture(String texturePath) {
		Texture texture = null;
		if (texturePath != null)
			texture = textureMap.get(texturePath);

		if (texture == null)
			texture = textureMap.get(DEFAULT_TEXTURE);

		return texture;
	}

	@Override
	public void onDispose() {
		textureMap.values().forEach(Texture::onDispose);
	}

}
