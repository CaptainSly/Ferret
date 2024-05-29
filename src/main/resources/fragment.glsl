#version 330

in vec2 outTextCoords;

out vec4 fragColor;

struct Material {
	vec4 diffuse;
};

uniform sampler2D textureSampler;
uniform Material material;

void main() {
	
	fragColor = texture(textureSampler, outTextCoords) + material.diffuse;
	
}