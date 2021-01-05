package sqlclient.cli.sinks;

import javax.annotation.PostConstruct;

import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class OutputSinkConsole extends AbstractOutputSink{
	@Autowired private LineReader lineReader;
	
	@PostConstruct
	public void init() {
	}

	@Override
	public void printInfo(String string) {
		this.lineReader.printAbove(string);
	}
	@Override
	public void printError(String string) {
		System.err.println(string);
	}
	
	@Override
	public void write(String string) {
		System.out.print(string);
	}
	@Override
	public void writeLine(String string) {
		System.out.println(string);
	}
	public void destroy() throws Exception {
		System.out.flush();
	}
}
