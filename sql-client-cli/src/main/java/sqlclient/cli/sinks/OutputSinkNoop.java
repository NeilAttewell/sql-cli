package sqlclient.cli.sinks;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class OutputSinkNoop extends AbstractOutputSink{
	public OutputSinkNoop() {
	}
	@PostConstruct
	public void init() {
	}
	@Override
	public void printInfo(String string) {
	}
	@Override
	public void printError(String string) {
	}
	@Override
	public void write(String string) {
	}
	@Override
	public void writeLine(String string) {
	}
	public void destroy() throws Exception {
	}
}
