package sqlclient.cli.sinks;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class OutputSinkStandardOut extends AbstractOutputSink{
	@PostConstruct
	public void init() {
	}
	@Override
	public void printInfo(String string) {
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
