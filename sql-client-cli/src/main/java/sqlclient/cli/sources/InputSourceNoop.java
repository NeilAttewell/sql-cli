package sqlclient.cli.sources;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.vavr.Tuple2;

/**
 * @author Neil Attewell
 */
@Component
public class InputSourceNoop extends AbstractInputSource{
	@PostConstruct
	public void init() {
	}
	@Override
	public Tuple2<String, Boolean> readLine() {
		return null;
	}
	public void destroy() throws Exception {
	}
}
