package sqlclient.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import sqlclient.cli.z_boot.util.cli.CommandLine;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;

/**
 * POSIX like options (ie. tar -zxvf foo.tar.gz)
 * GNU like long options (ie. du --human-readable --max-depth=1)
 * Java like properties (ie. java -Djava.awt.headless=true -Djava.net.useSystemProxies=true Foo)
 * Short options with value attached (ie. gcc -O2 foo.c)
 * long options with single hyphen (ie. ant -projecthelp)
 * 
 * @author attewell
 */
@SpringBootConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class CommandLineParserTest {
	private static final CommandLine COMMAND_LINE = CommandLineBuilder.builder()
			.addOption().withShortName('a').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('b').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('c').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('d').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('e').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('f').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('g').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('h').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('j').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('k').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('p').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('q').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('r').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('s').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('t').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('u').withLongName(null).withHasValue(true).build()
			.addOption().withShortName('v').withLongName(null).withHasValue(false).build()
			.addOption().withShortName('w').withLongName(null).withHasValue(true).build()

			.addOption().withShortName(null).withLongName("argA").withHasValue(false).build()
			.addOption().withShortName(null).withLongName("argB").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argC").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argD").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argE").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argF").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argG").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argH").withHasValue(true).build()
			.addOption().withShortName('i').withLongName("argI").withHasValue(true).build()
			.addOption().withShortName(null).withLongName("argJ").withHasValue(false).build()
			.addOption().withShortName(null).withLongName("argK").withHasValue(true).build()
			.build(new String[] {
					"-a","-bvb","-c=vc","-d","vd","-eve1","-eve2","-f=vf1","-f=vf2","-g","vg1","-g","vg2","-h","vh1","-hvh2","-h=vh3",
					"--argA","--argBvB","--argC=vC","--argD","vD","--argEvE1","--argEvE2","--argF=vF1","--argF=vF2","--argG","vG1","--argG","vG2","--argH","vH1","--argHvH2","--argH=vH3",
					"-i","vI1","--argI","vI2",
					"-j", "valueJ1", "--argJ", "valueJ2",
					"-k", "--argK",
					"-l", "--argL", "-m", "vm", "--argM", "vM",
					"-pqrs", "vs", "-tuvu", "-vw=vw"});


	@Test
	public void shortNameNoValue() {
		Assertions.assertTrue(COMMAND_LINE.isFound('a'));
		Assertions.assertNull(COMMAND_LINE.getValue('a'));

		Assertions.assertTrue(COMMAND_LINE.isFound('j'));
		Assertions.assertNull(COMMAND_LINE.getValue('j'));
	}
	@Test
	public void shortNameValueWithNoSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues('b').size(),1);
		Assertions.assertEquals(COMMAND_LINE.getValue('b'),"vb");
	}
	@Test
	public void shortNameValueWithEquals() {
		Assertions.assertEquals(COMMAND_LINE.getValues('c').size(),1);
		Assertions.assertEquals(COMMAND_LINE.getValue('c'),"vc");
	}
	@Test
	public void shortNameValueWithSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues('d').size(),1);
		Assertions.assertEquals(COMMAND_LINE.getValue('d'),"vd");
	}
	@Test
	public void shortNameValuesWithNoSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues('e').size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue('e'),"ve1");
		Assertions.assertEquals(COMMAND_LINE.getValues('e').get(0),"ve1");
		Assertions.assertEquals(COMMAND_LINE.getValues('e').get(1),"ve2");
	}
	@Test
	public void shortNameValuesWithEquals() {
		Assertions.assertEquals(COMMAND_LINE.getValues('f').size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue('f'),"vf1");
		Assertions.assertEquals(COMMAND_LINE.getValues('f').get(0),"vf1");
		Assertions.assertEquals(COMMAND_LINE.getValues('f').get(1),"vf2");
	}
	@Test
	public void shortNameValuesWithSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues('g').size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue('g'),"vg1");
		Assertions.assertEquals(COMMAND_LINE.getValues('g').get(0),"vg1");
		Assertions.assertEquals(COMMAND_LINE.getValues('g').get(1),"vg2");
	}
	@Test
	public void shortNameValuesMixed() {
		Assertions.assertEquals(COMMAND_LINE.getValues('h').size(),3);
		Assertions.assertEquals(COMMAND_LINE.getValue('h'),"vh1");
		Assertions.assertEquals(COMMAND_LINE.getValues('h').get(0),"vh1");
		Assertions.assertEquals(COMMAND_LINE.getValues('h').get(1),"vh2");
		Assertions.assertEquals(COMMAND_LINE.getValues('h').get(2),"vh3");
	}

	@Test
	public void longNameNoValue() {
		Assertions.assertTrue(COMMAND_LINE.isFound("argA"));
		Assertions.assertNull(COMMAND_LINE.getValue("argA"));
		
		Assertions.assertTrue(COMMAND_LINE.isFound("argJ"));
		Assertions.assertNull(COMMAND_LINE.getValue("argJ"));
	}
	@Test
	public void longNameValueWithNoSpace() {
		Assertions.assertFalse(COMMAND_LINE.isFound("argB"));
	}
	@Test
	public void longNameValueWithEquals() {
		Assertions.assertEquals(COMMAND_LINE.getValues("argC").size(),1);
		Assertions.assertEquals(COMMAND_LINE.getValue("argC"),"vC");
	}
	@Test
	public void longNameValueWithSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues("argD").size(),1);
		Assertions.assertEquals(COMMAND_LINE.getValue("argD"),"vD");
	}
	@Test
	public void longNameValuesWithNoSpace() {
		Assertions.assertFalse(COMMAND_LINE.isFound("argE"));
	}
	@Test
	public void longNameValuesWithEquals() {
		Assertions.assertEquals(COMMAND_LINE.getValues("argF").size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue("argF"),"vF1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argF").get(0),"vF1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argF").get(1),"vF2");
	}
	@Test
	public void longNameValuesWithSpace() {
		Assertions.assertEquals(COMMAND_LINE.getValues("argG").size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue("argG"),"vG1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argG").get(0),"vG1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argG").get(1),"vG2");
	}
	@Test
	public void longNameValuesMixed() {
		Assertions.assertEquals(COMMAND_LINE.getValues("argH").size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue("argH"),"vH1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argH").get(0),"vH1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argH").get(1),"vH3");
	}

	@Test
	public void shortAndLongWithValues() {
		Assertions.assertEquals(COMMAND_LINE.getValues('i').size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue('i'),"vI1");
		Assertions.assertEquals(COMMAND_LINE.getValues('i').get(0),"vI1");
		Assertions.assertEquals(COMMAND_LINE.getValues('i').get(1),"vI2");

		Assertions.assertEquals(COMMAND_LINE.getValues("argI").size(),2);
		Assertions.assertEquals(COMMAND_LINE.getValue("argI"),"vI1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argI").get(0),"vI1");
		Assertions.assertEquals(COMMAND_LINE.getValues("argI").get(1),"vI2");
	}

	@Test
	public void shortNameMissingValue() {
		Assertions.assertTrue(COMMAND_LINE.isFound('k'));
		Assertions.assertNull(COMMAND_LINE.getValue('k'));
		Assertions.assertTrue(COMMAND_LINE.getValues('k').isEmpty());
	}
	@Test
	public void longNameMissingValue() {
		Assertions.assertTrue(COMMAND_LINE.isFound("argK"));
		Assertions.assertNull(COMMAND_LINE.getValue("argK"));
		Assertions.assertTrue(COMMAND_LINE.getValues("argK").isEmpty());
	}

	@Test
	public void unknownShortNameNoValue() {
		Assertions.assertFalse(COMMAND_LINE.isFound('l'));
		Assertions.assertNull(COMMAND_LINE.getValue('l'));
	}
	@Test
	public void unknownLongNameNoValue() {
		Assertions.assertFalse(COMMAND_LINE.isFound("argL"));
		Assertions.assertNull(COMMAND_LINE.getValue("argL"));
	}
	@Test
	public void unknownShortNameHasValue() {
		Assertions.assertFalse(COMMAND_LINE.isFound('m'));
		Assertions.assertNull(COMMAND_LINE.getValue('m'));
	}
	@Test
	public void unknownLongNameHasValue() {
		Assertions.assertFalse(COMMAND_LINE.isFound("argM"));
		Assertions.assertNull(COMMAND_LINE.getValue("argM"));
	}
	@Test
	public void unknownMissingShortName() {
		Assertions.assertFalse(COMMAND_LINE.isFound('n'));
		Assertions.assertNull(COMMAND_LINE.getValue('n'));
	}
	@Test
	public void unknownMissingLongName() {
		Assertions.assertFalse(COMMAND_LINE.isFound("argO"));
		Assertions.assertNull(COMMAND_LINE.getValue("argO"));
	}

//	@Test
//	public void posixNoValue() {
//		Assertions.assertTrue(COMMAND_LINE.isFound('p'));
//		Assertions.assertNull(COMMAND_LINE.getValue('p'));
//		Assertions.assertTrue(COMMAND_LINE.isFound('q'));
//		Assertions.assertNull(COMMAND_LINE.getValue('q'));
//		Assertions.assertTrue(COMMAND_LINE.isFound('r'));
//		Assertions.assertNull(COMMAND_LINE.getValue('r'));
//		Assertions.assertTrue(COMMAND_LINE.isFound('t'));
//		Assertions.assertNull(COMMAND_LINE.getValue('t'));
//	}
//	@Test
//	public void posixWithValue() {
//		Assertions.assertTrue(COMMAND_LINE.isFound('s'));
//		Assertions.assertEquals(COMMAND_LINE.getValue('s'),"vs");
//	}
//	@Test
//	public void posixWithValueNoSpace() {
//		Assertions.assertTrue(COMMAND_LINE.isFound('u'));
//		Assertions.assertEquals(COMMAND_LINE.getValue('u'),"vu");
//	}
//	@Test
//	public void posixWithValueEquals() {
//		Assertions.assertTrue(COMMAND_LINE.isFound('w'));
//		Assertions.assertEquals(COMMAND_LINE.getValue('w'),"vw");
//	}
}
