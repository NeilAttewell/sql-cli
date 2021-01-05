package sqlclient.cli.sources;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.domain.Query;
import sqlclient.cli.z_boot.util.SqlParserUtils;

/**
 * @author Neil Attewell
 */
@Component
public class InputSourceStandardIn extends AbstractInputSource{
	@Autowired private SpecialCharacterRegistry registry;
	@PostConstruct
	public void init() {
	}
	@Override
	public Tuple2<String, Boolean> readLine() throws IOException {
		int i;
		String input = "";
		while((i=System.in.read()) != -1) {
			input += (char)i;
			Query query = SqlParserUtils.parse(input, registry)
					.stream()
					.findFirst()
					.orElse(null);
			if(query == null) {
				continue;
			}
			if(query.getDelimiter() == null) {
				continue;
			}
			var out = Tuple.of(input, false);
			input = null;
			return out;
		}
		if(StringUtils.isBlank(input)) {
			return null;
		}
		var out = Tuple.of(input, false);
		input = null;
		return out;
	}
	public void destroy() throws Exception {
	}
}
