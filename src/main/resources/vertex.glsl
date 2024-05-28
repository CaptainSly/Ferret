#version 330

layout (location=0) in vec3 inPosition;
layout (location=1) in vec2 inTextCoords;

out vec2 outTextCoords;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(inPosition, 1.0);
	outTextCoords = inTextCoords;	
}