package io.azraein.ferret.system.gfx.model;

import static org.lwjgl.assimp.Assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import io.azraein.ferret.system.gfx.mesh.Mesh;
import io.azraein.ferret.system.gfx.textures.TextureCache;

public class ModelLoader {

	private ModelLoader() {
	}

	public static Model loadModel(String modelId, String modelPath, TextureCache textureCache) {
		return loadModel(modelId, modelPath, textureCache,
				aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
						| aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights
						| aiProcess_PreTransformVertices);

	}

	public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags) {
		File file = new File(modelPath);
		if (!file.exists())
			throw new RuntimeException("Model path does not exist: " + modelPath);

		String modelDir = file.getParent();

		AIScene aiScene = aiImportFile(modelPath, flags);
		if (aiScene == null)
			throw new RuntimeException("Error loading Model: " + modelPath);

		int numMaterials = aiScene.mNumMaterials();
		List<Material> materialList = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
			AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
			materialList.add(processMaterial(aiMaterial, modelDir, textureCache));
		}

		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		Material defaultMaterial = new Material();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
			Mesh mesh = processMesh(aiMesh);
			int materialIdx = aiMesh.mMaterialIndex();
			Material material;

			if (materialIdx >= 0 && materialIdx < materialList.size())
				material = materialList.get(materialIdx);
			else
				material = defaultMaterial;

			material.getMeshList().add(mesh);
		}

		if (!defaultMaterial.getMeshList().isEmpty())
			materialList.add(defaultMaterial);

		return new Model(modelId, materialList);
	}

	private static Mesh processMesh(AIMesh aiMesh) {
		float[] vertices = processVertices(aiMesh);
		float[] normals = processNormals(aiMesh);
		float[] texCoords = processTexCoords(aiMesh);
		int[] indices = processIndices(aiMesh);

		if (texCoords.length == 0) {
			int numElements = (vertices.length / 3) * 2;
			texCoords = new float[numElements];
		}

		return new Mesh(vertices, normals, texCoords, indices);
	}
	
	private static float[] processNormals(AIMesh aiMesh) {
		AIVector3D.Buffer normals = aiMesh.mNormals();
		float[] data = new float[normals.remaining() * 3];
		int pos = 0;
		while (normals.remaining() > 0) {
			AIVector3D normal = normals.get();
			data[pos++] = normal.x();
			data[pos++] = normal.y();
			data[pos++] = normal.z();
		}
		
		return data;
	}

	private static int[] processIndices(AIMesh aiMesh) {
		List<Integer> indices = new ArrayList<>();
		int numFaces = aiMesh.mNumFaces();
		AIFace.Buffer aiFaces = aiMesh.mFaces();
		for (int i = 0; i < numFaces; i++) {
			AIFace aiFace = aiFaces.get(i);
			IntBuffer buffer = aiFace.mIndices();
			while (buffer.remaining() > 0) {
				indices.add(buffer.get());
			}
		}
		return indices.stream().mapToInt(Integer::intValue).toArray();
	}

	private static float[] processTexCoords(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
		if (buffer == null) {
			return new float[] {};
		}
		float[] data = new float[buffer.remaining() * 2];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D textCoord = buffer.get();
			data[pos++] = textCoord.x();
			data[pos++] = 1 - textCoord.y();
		}
		return data;
	}

	private static float[] processVertices(AIMesh aiMesh) {
		AIVector3D.Buffer buffer = aiMesh.mVertices();
		float[] data = new float[buffer.remaining() * 3];
		int pos = 0;
		while (buffer.remaining() > 0) {
			AIVector3D vertex = buffer.get();
			data[pos++] = vertex.x();
			data[pos++] = vertex.y();
			data[pos++] = vertex.z();
		}
		return data;
	}

	private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
		Material material = new Material();
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AIColor4D color = AIColor4D.create();

			int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS) {
				material.setAmbientColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}
			
			result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS) {
				material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}

			result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
			if (result == aiReturn_SUCCESS) {
				material.setSpecularColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
			}

			float reflectance = 0.0f;
			float[] shininessFactor = new float[] { 0.0f };
			int[] pMax = new int[] { 1 };
			result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0,
					shininessFactor, pMax);
			if (result != aiReturn_SUCCESS) {
				reflectance = shininessFactor[0];
			}
			material.setReflectance(reflectance);

			AIString aiTexturePath = AIString.calloc(stack);
			aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null,
					null, null, null);

			String texturePath = aiTexturePath.dataString();
			if (texturePath != null && texturePath.length() > 0) {
				material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
				textureCache.createTexture(material.getTexturePath());
				material.setDiffuseColor(Material.DEFAULT_COLOR);
			}

		}
		return material;
	}
}
