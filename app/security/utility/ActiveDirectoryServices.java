package security.utility;

import play.Play;
import play.libs.F;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * Created by nareshdon on 04-12-2015.
 */
public class ActiveDirectoryServices {

    public static final String ldapURL = Play.application().configuration().getString("ActiveDirectory.url");
    public static final String domainName =   Play.application().configuration().getString("ActoveDirectory.DomainName");
    public static final int timeout =         Play.application().configuration().getInt("ActoveDirectory.timeout");
    private static final String account = "account";
    private static final String pass = "password";

    public static F.Promise<Boolean> authenticate(String username, String password) throws AuthenticationException, CommunicationException, NamingException {

        Hashtable<String, String> env = new Hashtable<String,String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", ""+(timeout*1000));
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username+domainName);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext authContext = null;
        authContext = new InitialDirContext(env);
        return F.Promise.pure(Boolean.TRUE);
    }

}
