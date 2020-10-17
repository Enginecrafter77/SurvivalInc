package enginecrafter77.survivalinc.client;

public class ImmutableElementPosition extends ElementPositioner {
	
	public ImmutableElementPosition(float mulX, float mulY, int offX, int offY)
	{
		this.mulX = mulX;
		this.mulY = mulY;
		this.offX = offX;
		this.offY = offY;
	}
	
	@Override
	public void setPositionOrigin(float x, float y)
	{
		throw new UnsupportedOperationException("Cannot change origin of an immutable position!");
	}
	
	@Override
	public void setPositionOffset(int x, int y)
	{
		throw new UnsupportedOperationException("Cannot change offset of an immutable position!");
	}
	
}
