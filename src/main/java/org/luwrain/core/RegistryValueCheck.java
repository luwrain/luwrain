

public class RegistryValueCheck
{
    private String logger = "";

    public int intAny(String path, int defaultValue);
    public int intPositive(String path, int defaultValue);
    public int intPositiveNotZero(String path, int defaultValue);
    public int intRange(String path, int min, int max, int defaultValue);

    public String strNotEmpty(String path, String defaultValue)
}
