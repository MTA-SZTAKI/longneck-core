package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.access.DatabaseTarget;
import hu.sztaki.ilab.longneck.process.access.QueryParseException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class QueryParseTest {

    @Test
    public void testGetPlaceholderMap() throws QueryParseException {
        Map<String,String> expected = new HashMap<String,String>();
        expected.put("request_url", "requestUrl");
        expected.put("request_url_extension", "requestUrlExtension");
        expected.put("referer", "referer");
        expected.put("host", "host");
        expected.put("user_agent", "userAgent");
        expected.put("ip_and_user_agent_hash", "ipAndUserAgentHash");
        expected.put("apache_cookie", "apacheCookie");
        expected.put("google_cookie", "googleCookie");
        expected.put("local_id", "localId");
        expected.put("global_id", "globalId");
                
        String query = "insert into userid_test " +
                  "(request_url, request_url_extension, referer, host, user_agent, ip_and_user_agent_hash, " +
                  "apache_cookie, google_cookie, local_id, global_id) " +
                  "values (:requestUrl, :requestUrlExtension, :referer, :host, :userAgent, :ipAndUserAgentHash, " +
                  ":apacheCookie, :googleCookie, :localId, :globalId)";

        Map<String,String> result = DatabaseTarget.getPlaceholderMap(query);
        
        Assert.assertEquals(expected, result);
    }
}
