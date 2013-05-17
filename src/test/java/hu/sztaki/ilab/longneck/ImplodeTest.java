package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Tibor Németh <tnemeth@sztaki.mta.hu>
 */
public class ImplodeTest {

  @Test
  public void testImplode1() {
    List<String> input = Arrays.asList(new String[] { null, "a", "b", null, "c", null, "", "e", null });

    String output = LongneckStringUtils.implode(",", input, true);
    Assert.assertEquals("a,b,c,e", output);
  }

  @Test
  public void testImplode2() {
    List<String> input = Arrays.asList(new String[] { null, "a", "b", null, "c", null, "", "e", null });

    String output = LongneckStringUtils.implode(",", input, false);
    Assert.assertEquals(",a,b,,c,,,e,", output);
  }

}
