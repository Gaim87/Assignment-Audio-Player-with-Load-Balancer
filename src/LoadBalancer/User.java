package LoadBalancer;

public class User
{
    String _username;
    String _password;
    
    public User (String username, String password)
    {
        _username = username;
        _password = password;
    }
    
    public String getUsername()
    {
        return _username;
    }
    
    public String getPassword()
    {
        return _password;
    }
}
