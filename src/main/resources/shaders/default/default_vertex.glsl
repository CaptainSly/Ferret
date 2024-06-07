#version 330

layout (location=0) in vec3 inPosition;
layout (location=1) in vec3 inNormal;
layout (location=2) in vec2 inTextCoords;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextCoords;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	vec4 movePosition = modelViewMatrix * vec4(inPosition, 1.0);
	gl_Position = projectionMatrix * movePosition;
	outPosition = movePosition.xyz;
	outNormal = normalize(modelViewMatrix * vec4(inNormal, 0.0)).xyz;
	outTextCoords = inTextCoords;
}