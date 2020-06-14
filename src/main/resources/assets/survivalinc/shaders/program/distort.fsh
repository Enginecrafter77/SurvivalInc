#version 120

// This is a basic screen distortion pattern
// which accompanies creepy static noise.
// This shader is rather simple, and most
// of the code was copied over from vanilla
// code. Author: Enginecrafter77

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Time;
uniform vec2 Frequency;
uniform vec2 WobbleAmount;

void main()
{
	// Original vanilla code. I decided to keep it there for reference, since I am not very good at GL shaders.
	//float xOffset = sin(texCoord.y * Frequency.x + Time * 3.1415926535 * 2.0) * WobbleAmount.x;
	//float yOffset = cos(texCoord.x * Frequency.y + Time * 3.1415926535 * 2.0) * WobbleAmount.y;
	//vec2 offset = vec2(xOffset, yOffset);
	
	float pi = 3.1415926535;
	float phase = sin(Time * pi);
	float xOffset = sin(texCoord.y * phase * Frequency.x * pi * 2.0) * WobbleAmount.x;
	float yOffset = cos(texCoord.x * phase * Frequency.y * pi * 2.0) * WobbleAmount.y;
	vec2 offset = vec2(xOffset, yOffset);
	
	vec4 rgb = texture2D(DiffuseSampler, texCoord + offset);
	gl_FragColor = vec4(rgb.rgb, 1.0);
}
