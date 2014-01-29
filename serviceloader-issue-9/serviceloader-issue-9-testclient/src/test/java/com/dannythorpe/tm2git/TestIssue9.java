package com.dannythorpe.tm2git;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;


public class TestIssue9
{
    /**
     * Test code to reproduce the now-you-see-it, now-you-don't issue
     * when using the serviceloader-maven-plugin with m2e.
     * <p>
     * If you run this as a pure Maven build (or even Run As -> Maven Install
     * under Eclipse), it's fine.
     * <p>
     * The fun starts if the most recent build of serviceloader-issue-9-service
     * was an Eclipse build (not a Run As -> Maven Install build).  To see
     * this...
     * <p>
     * First, the correct (now-you-see-it) behaviour:
     * <ul>
     * <li>Select the parent project (serviceloader-issue-9) and right-click
     * Run As -> Maven clean.  That should run successfully.  Then do the same
     * but use Run As -> Maven install, which should also succeed.</li>
     * <li>Select this project (serviceloader-issue-9-testclient) and
     * right-click Run As -> JUnit Test.  The Eclipse JUnit infrastructure
     * should successfully execute this test and display green goodness.</li>
     * </ul>
     * <p>
     * Now, the incorrect behaviour:
     * <ul>
     * <li>Trigger an Eclipse build of the project that contains the plugin,
     * i.e. the serviceloader-issue-9-service.  One way to do this is to
     * select that project, the use the Project pulldown (on the top menu bar)
     * to select Project -> Clean... for that project.  (Then ensure that
     * Eclipse builds the project after the clean, either because
     * Project -> Build Automatically is checked or because you then manually
     * invoke Project -> Build Project)</li>
     * <li>Now reissue the Run As -> JUnit Test in this project.  This time
     * the test should fail at the first assertion below.
     * </ul>
     * (The steps above voluntarily trigger an Eclipse build, but in real use
     * the Eclipse build is involuntary, triggered by various circumstances,
     * i.e. not so easy to avoid as "don't do that").
     * <p>
     * What's happening here is that, because I configured the m2e
     * lifecycleMappingMetadata for the serviceloader-maven-plugin to use the
     * &lt;ignore&gt; action, the Eclipse-with-m2e rebuild doesn't execute the
     * plugin, so not surprisingly (at least in retrospect) the build ends up
     * lacking the META-INF/services entry.  But I can't just change the action
     * to be &lt;execute&gt;, because worse things can happen if you use that
     * to execute a plugin that doesn't do things in an m2e-compatible way.
     * <p>
     * (Yes, from the "right-click" phrasing above and the EOL characters in
     * these files you can safely bet that I'm not using MacOS).
     */
    @Test
    public void testServicesEntryHeisenbug()
    {
        Iterator<IDummyService> loader =
            ServiceLoader.load(IDummyService.class).iterator();
        assertTrue("Service implementation should have been found but wasn't",
            loader.hasNext());

        // (The assertion above is the only one that matters;
        // the ones below are just here for completeness)
        IDummyService impl = loader.next();
        assertEquals("Got wrong implementation class!?",
             DummyServiceImpl.class, impl.getClass());
        assertFalse("Should only find one service implementation",
             loader.hasNext());
    }
}
