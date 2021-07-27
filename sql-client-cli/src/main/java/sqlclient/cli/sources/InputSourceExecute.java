package sqlclient.cli.sources;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.core.util.cli.CommandLine;

/**
 * @author Neil Attewell
 */
@Component
public class InputSourceExecute extends AbstractInputSource{
	@Autowired private CommandLine commandLine;
	private List<String> values = new ArrayList<>();

	@PostConstruct
	public void init() {
		this.values.addAll(this.commandLine.getValues('e'));
	}
	@Override
	public Tuple2<String, Boolean> readLine() {
		if(this.values.isEmpty()) {
			return null;
		}
		return Tuple.of(this.values.remove(0), true);
	}
	public void destroy() throws Exception {
	}
}
