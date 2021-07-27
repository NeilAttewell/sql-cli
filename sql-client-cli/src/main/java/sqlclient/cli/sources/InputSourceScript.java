package sqlclient.cli.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.core.util.cli.CommandLine;

/**
 * @author Neil Attewell
 */
@Component
public class InputSourceScript extends AbstractInputSource{
	@Autowired private CommandLine commandLine;
	private List<File> files = new ArrayList<>();
	private BufferedReader reader;


	@PostConstruct
	public void init() {
		this.files.addAll(this.commandLine.getValues('s').stream()
				.filter(StringUtils::isNotBlank)
				.map(File::new)
				.collect(Collectors.toList()));
	}
	@Override
	public Tuple2<String, Boolean> readLine() throws IOException {
		boolean newReader = this.reader == null;
		if(newReader) {
			if(this.files.isEmpty()) {
				return null;
			}

			File file = this.files.remove(0);
			this.reader = new BufferedReader(new FileReader(file));
		}

		String line = this.reader.readLine();
		if(line != null) {
			return Tuple.of(line, newReader);
		}
		this.reader.close();
		this.reader = null;
		return readLine();
	}
	public void destroy() throws Exception {
		if(this.reader != null) {
			this.reader.close();
			this.reader=null;
		}
	}
}
