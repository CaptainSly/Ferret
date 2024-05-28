#version 330

in vec2 outTextCoords;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
	
	fragColor = texture(textureSampler, outTextCoords);
	
}