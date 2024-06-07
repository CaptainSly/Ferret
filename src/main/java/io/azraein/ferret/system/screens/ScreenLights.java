package io.azraein.ferret.system.screens;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import io.azraein.ferret.system.gfx.Camera;
import io.azraein.ferret.system.gfx.lights.AmbientLight;
import io.azraein.ferret.system.gfx.lights.DirectionalLight;
import io.azraein.ferret.system.gfx.lights.PointLight;
import io.azraein.ferret.system.gfx.lights.SpotLight;
import io.azraein.ferret.system.gfx.shader.ShaderProgram;

public class ScreenLights {

	public static final int MAX_POINT_LIGHTS = 5;
	public static final int MAX_SPOT_LIGHTS = 5;

	private AmbientLight ambientLight;
	private DirectionalLight directionalLight;
	private Map<String, PointLight> pointLights;
	private Map<String, SpotLight> spotLights;

	public ScreenLights() {
		ambientLight = new AmbientLight();
		directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
		pointLights = new HashMap<>();
		spotLights = new HashMap<>();
	}

	public void createUniforms(ShaderProgram shaderProgram) {

		shaderProgram.createUniform("ambientLight.factor");
		shaderProgram.createUniform("ambientLight.color");

		for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
			String name = "pointLights[" + i + "]";
			shaderProgram.createUniform(name + ".position");
			shaderProgram.createUniform(name + ".color");
			shaderProgram.createUniform(name + ".intensity");
			shaderProgram.createUniform(name + ".att.constant");
			shaderProgram.createUniform(name + ".att.linear");
			shaderProgram.createUniform(name + ".att.exponent");
		}
		for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
			String name = "spotLights[" + i + "]";
			shaderProgram.createUniform(name + ".pl.position");
			shaderProgram.createUniform(name + ".pl.color");
			shaderProgram.createUniform(name + ".pl.intensity");
			shaderProgram.createUniform(name + ".pl.att.constant");
			shaderProgram.createUniform(name + ".pl.att.linear");
			shaderProgram.createUniform(name + ".pl.att.exponent");
			shaderProgram.createUniform(name + ".conedir");
			shaderProgram.createUniform(name + ".cutoff");
		}

		shaderProgram.createUniform("directionalLight.color");
		shaderProgram.createUniform("directionalLight.direction");
		shaderProgram.createUniform("directionalLight.intensity");
	}

	public void updateLights(ShaderProgram shaderProgram, Camera camera) {
		// Send Lights to the Shader
		// Ambient
		shaderProgram.setUniform("ambientLight.factor", ambientLight.getIntensity());
		shaderProgram.setUniform("ambientLight.color", ambientLight.getColor());

		// Directional Light
		Vector4f auxDir = new Vector4f(directionalLight.getDirection(), 0);
		auxDir.mul(camera.getViewMatrix());
		Vector3f direction = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
		shaderProgram.setUniform("directionalLight.color", directionalLight.getColor());
		shaderProgram.setUniform("directionalLight.direction", direction);
		shaderProgram.setUniform("directionalLight.intensity", directionalLight.getIntensity());

		// Point Lights
		Collection<PointLight> pLights = pointLights.values();
		int i = 0;
		for (PointLight point : pLights) {
			String name = "pointLights[" + i + "]";
			updatePointLight(point, name, camera.getViewMatrix(), shaderProgram);
			i++;
		}

		// Spot Lights
		Collection<SpotLight> sLights = spotLights.values();
		i = 0;
		for (SpotLight spot : sLights) {
			String name = "spotLights[" + i + "]";
			updateSpotLight(spot, name, camera.getViewMatrix(), shaderProgram);
			i++;
		}

	}

	private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix, ShaderProgram shaderProgram) {
		PointLight pointLight = null;
		Vector3f coneDirection = new Vector3f();
		float cutoff = 0.0f;
		if (spotLight != null) {
			coneDirection = spotLight.getConeDirection();
			cutoff = spotLight.getCutOff();
			pointLight = spotLight.getPointLight();
		}

		shaderProgram.setUniform(prefix + ".conedir", coneDirection);
		shaderProgram.setUniform(prefix + ".conedir", cutoff);
		updatePointLight(pointLight, prefix + ".pl", viewMatrix, shaderProgram);
	}

	private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix,
			ShaderProgram shaderProgram) {
		Vector4f aux = new Vector4f();
		Vector3f lightPosition = new Vector3f();
		Vector3f color = new Vector3f();

		if (pointLight != null) {
			float intensity = 0.0f;
			float constant = 0.0f;
			float linear = 0.0f;
			float exponent = 0.0f;
			if (pointLight != null) {
				aux.set(pointLight.getPosition(), 1);
				aux.mul(viewMatrix);
				lightPosition.set(aux.x, aux.y, aux.z);
				color.set(pointLight.getColor());
				intensity = pointLight.getIntensity();
				PointLight.Attenuation attenuation = pointLight.getAttenuation();
				constant = attenuation.getConstant();
				linear = attenuation.getLinear();
				exponent = attenuation.getExponent();
			}
			shaderProgram.setUniform(prefix + ".position", lightPosition);
			shaderProgram.setUniform(prefix + ".color", color);
			shaderProgram.setUniform(prefix + ".intensity", intensity);
			shaderProgram.setUniform(prefix + ".att.constant", constant);
			shaderProgram.setUniform(prefix + ".att.linear", linear);
			shaderProgram.setUniform(prefix + ".att.exponent", exponent);
		}
	}

	public AmbientLight getAmbientLight() {
		return ambientLight;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public Map<String, PointLight> getPointLights() {
		return pointLights;
	}

	public Map<String, SpotLight> getSpotLights() {
		return spotLights;
	}

	public void setAmbientLight(AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}

	public PointLight getPointLight(String lightId) {
		return pointLights.get(lightId);
	}

	public SpotLight getSpotLight(String lightId) {
		return spotLights.get(lightId);
	}

	public void setDirectionalLight(DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	public void addPointLight(PointLight point) {
		this.pointLights.put(point.getId(), point);
	}

	public void addSpotLights(SpotLight spot) {
		this.spotLights.put(spot.getId(), spot);
		addPointLight(spot.getPointLight());
	}

	public void removeSpotLight(String lightId) {
		var spot = spotLights.get(lightId);
		pointLights.remove(spot.getPointLight().getId());
		spotLights.remove(lightId);
	}

	public void removePointLight(String lightId) {
		pointLights.remove(lightId);
	}

}
