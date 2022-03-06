
// Potential for more states to this game
public abstract class State
{
    protected String region;
    public abstract void start(String region);

    public String getRegion()
    {
        return  region;
    }
}
