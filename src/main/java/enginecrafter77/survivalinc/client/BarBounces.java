package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Random;

public class BarBounces {

	/*
	 * It's a joke! When you give me that look, it's a jooooke... hehaha...
	 */

	private final Random rand = new Random();
	private int jokeDirX1 = 1, jokeDirY1 = 1, jokeDirX2 = 1, jokeDirY2 = 1, jokeDirX3 = 1, jokeDirY3 = 1, jokeDirX4 = 1,
			jokeDirY4 = 1;
	private int jokeX1 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY1 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX2 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY2 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX3 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY3 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX4 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY4 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());

	public int getJokeX(int screenWidth, int pos)
	{

		if (pos == 0)
		{

			jokeX1 += jokeDirX1;

			if (jokeX1 > screenWidth || jokeX1 < 100)
			{

				jokeDirX1 *= -1;
			}

			return jokeX1;
		}

		else if (pos == 1)
		{

			jokeX2 += jokeDirX2;

			if (jokeX2 > screenWidth || jokeX2 < 100)
			{

				jokeDirX2 *= -1;
			}

			return jokeX2;
		}

		else if (pos == 2)
		{

			jokeX3 += jokeDirX3;

			if (jokeX3 > screenWidth || jokeX3 < 100)
			{

				jokeDirX3 *= -1;
			}

			return jokeX3;
		}

		else
		{

			jokeX4 += jokeDirX4;

			if (jokeX4 > screenWidth || jokeX4 < 100)
			{

				jokeDirX4 *= -1;
			}

			return jokeX4;
		}
	}

	public int getJokeY(int screenHeight, int pos)
	{

		if (pos == 1)
		{

			jokeY1 += jokeDirY1;

			if (jokeY1 > screenHeight || jokeY1 < 0)
			{

				jokeDirY1 *= -1;
			}

			return jokeY1;
		}

		else if (pos == 2)
		{

			jokeY2 += jokeDirY2;

			if (jokeY2 > screenHeight || jokeY2 < 0)
			{

				jokeDirY2 *= -1;
			}

			return jokeY2;
		}

		else if (pos == 3)
		{

			jokeY3 += jokeDirY3;

			if (jokeY3 > screenHeight || jokeY3 < 0)
			{

				jokeDirY3 *= -1;
			}

			return jokeY3;
		}

		else
		{

			jokeY4 += jokeDirY4;

			if (jokeY4 > screenHeight || jokeY4 < 0)
			{

				jokeDirY4 *= -1;
			}

			return jokeY4;
		}
	}
}
